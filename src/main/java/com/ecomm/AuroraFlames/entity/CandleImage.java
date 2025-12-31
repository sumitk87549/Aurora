package com.ecomm.AuroraFlames.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(nullable = true)
    @JdbcTypeCode(SqlTypes.BLOB)
    private byte[] imageData;

    @Column(nullable = true)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candle_id")
    @JsonIgnoreProperties({ "images", "cartItems", "wishlistItems", "orderItems" })
    private Candle candle;
}
