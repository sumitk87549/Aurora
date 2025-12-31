package com.ecomm.AuroraFlames.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candle_id")
    private Candle candle;
    
    @Column(nullable = false)
    private LocalDateTime addedAt;
}
