package com.ecomm.AuroraFlames.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "candle_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String imageName;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "image_data")
    private byte[] imageData;

    @Column(nullable = true)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candle_id")
    @JsonIgnoreProperties({ "images", "cartItems", "wishlistItems", "orderItems" })
    private Candle candle;
}
