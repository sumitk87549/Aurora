package com.ecomm.AuroraFlames.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "candles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = true)
    private boolean creatorsChoice = false;

    @Column(columnDefinition = "TEXT")
    private String creatorsText;
    
    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CandleImage> images;
    
    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistItem> wishlistItems;
    
    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}
