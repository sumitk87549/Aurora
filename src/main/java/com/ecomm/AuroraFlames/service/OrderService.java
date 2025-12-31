package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.dto.CheckoutRequest;
import com.ecomm.AuroraFlames.dto.RazorpayOrderResponse;
import com.ecomm.AuroraFlames.entity.*;
import com.ecomm.AuroraFlames.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    @Value("${aurora.delivery.estimated-days:5}")
    private int estimatedDeliveryDays;

    @Value("${aurora.delivery.free-shipping-threshold:500}")
    private BigDecimal freeShippingThreshold;

    /**
     * Create order for COD payment
     */
    public Order createCODOrder(User user, CheckoutRequest checkoutRequest) {
        Order order = createOrderFromCart(user, checkoutRequest);
        order.setPaymentMethod("COD");
        order.setPaymentStatus("PENDING");
        order.setStatus("CONFIRMED");

        Order savedOrder = orderRepository.save(order);

        // Reduce stock
        reduceStock(savedOrder);

        // Clear cart
        cartService.clearCart(user);

        // Send notifications
        sendOrderNotifications(savedOrder);

        return savedOrder;
    }

    /**
     * Create order for Razorpay payment (initial creation before payment)
     */
    public RazorpayOrderResponse createRazorpayOrder(User user, CheckoutRequest checkoutRequest) throws Exception {
        Order order = createOrderFromCart(user, checkoutRequest);
        order.setPaymentMethod("RAZORPAY");
        order.setPaymentStatus("PENDING");
        order.setStatus("PAYMENT_PENDING");

        Order savedOrder = orderRepository.save(order);

        // Create Razorpay order
        RazorpayOrderResponse razorpayResponse = paymentService.createRazorpayOrder(savedOrder);

        // Update order with Razorpay order ID
        savedOrder.setRazorpayOrderId(razorpayResponse.getRazorpayOrderId());
        orderRepository.save(savedOrder);

        return razorpayResponse;
    }

    /**
     * Verify Razorpay payment and complete order
     */
    public Order completeRazorpayOrder(String orderNumber, String razorpayPaymentId,
            String razorpayOrderId, String razorpaySignature, User user) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));

        // Verify signature
        boolean isValid = paymentService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (!isValid) {
            order.setPaymentStatus("FAILED");
            order.setStatus("PAYMENT_FAILED");
            orderRepository.save(order);
            throw new RuntimeException("Payment verification failed");
        }

        // Payment successful
        order.setRazorpayPaymentId(razorpayPaymentId);
        order.setRazorpaySignature(razorpaySignature);
        order.setPaymentStatus("PAID");
        order.setStatus("CONFIRMED");

        Order savedOrder = orderRepository.save(order);

        // Reduce stock
        reduceStock(savedOrder);

        // Clear cart
        cartService.clearCart(user);

        // Send notifications
        sendOrderNotifications(savedOrder);

        return savedOrder;
    }

    /**
     * Create order from cart (common logic)
     */
    private Order createOrderFromCart(User user, CheckoutRequest checkoutRequest) {
        Cart cart = cartService.getCartByUser(user);

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate stock availability
        for (CartItem cartItem : cart.getCartItems()) {
            Candle candle = cartItem.getCandle();
            if (candle.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + candle.getName() +
                        ". Available: " + candle.getStockQuantity() + ", Requested: " + cartItem.getQuantity());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderItems(new ArrayList<>());
        order.setShippingAddress(checkoutRequest.getShippingAddress());
        order.setCustomerName(checkoutRequest.getCustomerName());
        order.setCustomerPhone(checkoutRequest.getCustomerPhone());
        order.setCustomerEmail(
                checkoutRequest.getCustomerEmail() != null ? checkoutRequest.getCustomerEmail() : user.getEmail());
        order.setOrderNotes(checkoutRequest.getOrderNotes());
        order.setOrderDate(LocalDateTime.now());
        order.setExpectedDeliveryDate(LocalDateTime.now().plusDays(estimatedDeliveryDays));

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCandle(cartItem.getCandle());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(cartItem.getPriceAtTime());

            order.getOrderItems().add(orderItem);

            BigDecimal itemTotal = cartItem.getPriceAtTime().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        order.setSubtotal(subtotal);

        // Calculate shipping (free above threshold)
        BigDecimal shippingCost = subtotal.compareTo(freeShippingThreshold) >= 0 ? BigDecimal.ZERO
                : new BigDecimal("50");
        order.setShippingCost(shippingCost);

        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(subtotal.add(shippingCost));

        return order;
    }

    /**
     * Reduce stock after successful order
     */
    private void reduceStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Candle candle = item.getCandle();

            // Re-fetch to ensure fresh data
            candle = candleRepository.findById(candle.getId()).orElse(candle);

            int currentStock = candle.getStockQuantity();
            int newStock = currentStock - item.getQuantity();

            if (newStock < 0) {
                // Determine what to do? For now, set to 0 and log error,
                // as we don't want to fail the order post-payment for this reason if possible.
                // Ideally this checked before payment.
                System.err.println("CRITICAL: Stock went negative for candle: " + candle.getName());
                newStock = 0;
            }

            candle.setStockQuantity(newStock);

            // Auto-mark as unavailable if out of stock
            if (candle.getStockQuantity() == 0) {
                candle.setAvailable(false);
            }

            candleRepository.save(candle);
        }
    }

    /**
     * Send email notifications
     */
    private void sendOrderNotifications(Order order) {
        try {
            if (order.getCustomerEmail() != null && !order.getCustomerEmail().isEmpty()) {
                emailService.sendOrderConfirmationToCustomer(order);
            }
            emailService.sendOrderNotificationToAdmin(order);
        } catch (Exception e) {
            System.err.println("Failed to send notifications: " + e.getMessage());
            // Don't fail the order if email fails
        }
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

        if ("DELIVERED".equals(status)) {
            order.setDeliveryDate(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    public java.util.Map<String, Object> getDashboardStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRevenue", orderRepository.getTotalRevenue());
        stats.put("todayRevenue", orderRepository.getTodayRevenue(java.time.LocalDate.now().atStartOfDay()));
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus("PENDING"));
        stats.put("confirmedOrders", orderRepository.countByStatus("CONFIRMED"));
        stats.put("processingOrders", orderRepository.countByStatus("PROCESSING")); // If you have this status
        stats.put("shippedOrders", orderRepository.countByStatus("SHIPPED"));
        stats.put("deliveredOrders", orderRepository.countByStatus("DELIVERED"));
        stats.put("cancelledOrders", orderRepository.countByStatus("CANCELLED"));
        return stats;
    }

    private String generateOrderNumber() {
        return "AF-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
