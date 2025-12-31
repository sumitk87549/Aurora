package com.ecomm.AuroraFlames.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String paymentMethod = "COD"; // COD or RAZORPAY

    // Customer contact details for this order
    @NotBlank(message = "Name is required")
    private String customerName;

    @NotBlank(message = "Phone is required")
    private String customerPhone;

    private String customerEmail;

    // City, state, pincode for shipping
    private String city;
    private String state;
    private String pincode;

    // Optional order notes
    private String orderNotes;

    // For Razorpay payments
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}
