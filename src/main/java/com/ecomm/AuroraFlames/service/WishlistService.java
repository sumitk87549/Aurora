package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.entity.*;
import com.ecomm.AuroraFlames.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Transactional
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private CandleRepository candleRepository;

    public Wishlist getWishlistByUser(User user) {
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(user);
            newWishlist.setWishlistItems(new ArrayList<>());
            return wishlistRepository.save(newWishlist);
        });
        // Force load wishlist items and their candles with images
        wishlist.getWishlistItems().forEach(item -> {
            item.getCandle().getImages().size();
        });
        return wishlist;
    }

    public Wishlist addToWishlist(User user, Long candleId) {
        Wishlist wishlist = getWishlistByUser(user);
        Candle candle = candleRepository.findById(candleId)
                .orElseThrow(() -> new RuntimeException("Candle not found"));

        boolean alreadyExists = wishlist.getWishlistItems().stream()
                .anyMatch(item -> item.getCandle().getId().equals(candleId));

        if (!alreadyExists) {
            WishlistItem newItem = new WishlistItem();
            newItem.setWishlist(wishlist);
            newItem.setCandle(candle);
            newItem.setAddedAt(LocalDateTime.now());
            wishlist.getWishlistItems().add(newItem);
        }

        return wishlistRepository.save(wishlist);
    }

    public Wishlist removeFromWishlist(User user, Long itemId) {
        Wishlist wishlist = getWishlistByUser(user);
        WishlistItem item = wishlist.getWishlistItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        wishlist.getWishlistItems().remove(item);
        wishlistItemRepository.delete(item);

        return wishlistRepository.save(wishlist);
    }
}
