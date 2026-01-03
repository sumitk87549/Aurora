package com.ecomm.AuroraFlames.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Boolean creatorsChoice = false;

    @Column(columnDefinition = "TEXT")
    private String creatorsText;

    @Column(nullable = true)
    private Boolean featured = false;

    @Column(nullable = true)
    private String category; // e.g., "Love", "Christmas", "Relaxation", "Spa", "Seasonal"

    @Column(nullable = true)
    private String fragrance; // e.g., "Lavender", "Vanilla", "Rose"

    @Column(nullable = true)
    private String color; // e.g., "White", "Red", "Gold"

    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "candle" })
    private List<CandleImage> images;

    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<WishlistItem> wishlistItems;

    @OneToMany(mappedBy = "candle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<OrderItem> orderItems;
}
