package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Wishlist;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "http://localhost:4200")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<Wishlist> getWishlist(@AuthenticationPrincipal User user) {
        Wishlist wishlist = wishlistService.getWishlistByUser(user);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/add")
    public ResponseEntity<Wishlist> addToWishlist(@AuthenticationPrincipal User user, 
                                                   @RequestParam Long candleId) {
        Wishlist wishlist = wishlistService.addToWishlist(user, candleId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Wishlist> removeFromWishlist(@AuthenticationPrincipal User user,
                                                      @PathVariable Long itemId) {
        Wishlist wishlist = wishlistService.removeFromWishlist(user, itemId);
        return ResponseEntity.ok(wishlist);
    }
}
