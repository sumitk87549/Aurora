package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.entity.Cart;
import com.ecomm.AuroraFlames.entity.CartItem;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal User user) {
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal User user, 
                                          @RequestParam Long candleId, 
                                          @RequestParam Integer quantity) {
        Cart cart = cartService.addToCart(user, candleId, quantity);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<Cart> updateCartItem(@AuthenticationPrincipal User user,
                                               @PathVariable Long itemId,
                                               @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartItem(user, itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Cart> removeFromCart(@AuthenticationPrincipal User user,
                                               @PathVariable Long itemId) {
        Cart cart = cartService.removeFromCart(user, itemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
}
