package com.ecomm.AuroraFlames.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private CandleDTO candle;
    private Integer quantity;
    private BigDecimal priceAtTime;
}
