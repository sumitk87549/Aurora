package com.ecomm.AuroraFlames.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    private String paymentMethod = "COD";
}
