package com.ecomm.AuroraFlames.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "password" })
    private User user;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({ "order" })
    private List<OrderItem> orderItems;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Subtotal before any discounts
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Shipping cost
    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    // Discount amount
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "PENDING";

    // Payment method: COD or RAZORPAY
    @Column(nullable = false)
    private String paymentMethod;

    // Payment status: PENDING, PAID, FAILED, REFUNDED
    @Column(nullable = false)
    private String paymentStatus = "PENDING";

    // Razorpay specific fields
    @Column
    private String razorpayOrderId;

    @Column
    private String razorpayPaymentId;

    @Column
    private String razorpaySignature;

    // Shipping address as JSON string or simple text
    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    // Contact details for delivery
    @Column
    private String customerName;

    @Column
    private String customerPhone;

    @Column
    private String customerEmail;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column
    private LocalDateTime expectedDeliveryDate;

    @Column
    private LocalDateTime deliveryDate;

    // Order notes from customer
    @Column(columnDefinition = "TEXT")
    private String orderNotes;

    // Admin notes (internal)
    @Column(columnDefinition = "TEXT")
    private String adminNotes;
}
