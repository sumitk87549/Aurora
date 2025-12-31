package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.dto.CheckoutRequest;
import com.ecomm.AuroraFlames.dto.RazorpayOrderResponse;
import com.ecomm.AuroraFlames.entity.Order;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.service.OrderService;
import com.ecomm.AuroraFlames.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    /**
     * Checkout with Cash on Delivery
     */
    @PostMapping("/checkout/cod")
    public ResponseEntity<Order> checkoutCOD(@AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest checkoutRequest) {
        Order order = orderService.createCODOrder(user, checkoutRequest);
        return ResponseEntity.ok(order);
    }

    /**
     * Create Razorpay order for online payment
     */
    @PostMapping("/checkout/razorpay/create")
    public ResponseEntity<?> createRazorpayOrder(@AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            if (!paymentService.isRazorpayConfigured()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Razorpay is not configured. Please use Cash on Delivery.",
                        "razorpayConfigured", false));
            }
            RazorpayOrderResponse response = orderService.createRazorpayOrder(user, checkoutRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify Razorpay payment and complete order
     */
    @PostMapping("/checkout/razorpay/verify")
    public ResponseEntity<?> verifyRazorpayPayment(@AuthenticationPrincipal User user,
            @RequestBody Map<String, String> paymentData) {
        try {
            String orderNumber = paymentData.get("orderNumber");
            String razorpayPaymentId = paymentData.get("razorpay_payment_id");
            String razorpayOrderId = paymentData.get("razorpay_order_id");
            String razorpaySignature = paymentData.get("razorpay_signature");

            Order order = orderService.completeRazorpayOrder(orderNumber, razorpayPaymentId,
                    razorpayOrderId, razorpaySignature, user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check if Razorpay is configured
     */
    @GetMapping("/payment/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfig() {
        return ResponseEntity.ok(Map.of(
                "razorpayConfigured", paymentService.isRazorpayConfigured(),
                "razorpayKeyId", paymentService.isRazorpayConfigured() ? paymentService.getRazorpayKeyId() : ""));
    }

    /**
     * Get user's orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User user) {
        List<Order> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(@AuthenticationPrincipal User user,
            @PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(order);
    }
}
