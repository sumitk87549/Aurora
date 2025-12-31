package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.dto.WishlistDTO;
import com.ecomm.AuroraFlames.entity.Wishlist;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.service.WishlistService;
import com.ecomm.AuroraFlames.util.DTOMapper;
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
    
    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping
    public ResponseEntity<WishlistDTO> getWishlist(@AuthenticationPrincipal User user) {
        Wishlist wishlist = wishlistService.getWishlistByUser(user);
        WishlistDTO wishlistDTO = dtoMapper.toWishlistDTO(wishlist);
        return ResponseEntity.ok(wishlistDTO);
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistDTO> addToWishlist(@AuthenticationPrincipal User user, 
                                                   @RequestParam Long candleId) {
        Wishlist wishlist = wishlistService.addToWishlist(user, candleId);
        WishlistDTO wishlistDTO = dtoMapper.toWishlistDTO(wishlist);
        return ResponseEntity.ok(wishlistDTO);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<WishlistDTO> removeFromWishlist(@AuthenticationPrincipal User user,
                                                      @PathVariable Long itemId) {
        Wishlist wishlist = wishlistService.removeFromWishlist(user, itemId);
        WishlistDTO wishlistDTO = dtoMapper.toWishlistDTO(wishlist);
        return ResponseEntity.ok(wishlistDTO);
    }
}
