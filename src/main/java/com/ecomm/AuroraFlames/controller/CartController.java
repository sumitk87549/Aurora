package com.ecomm.AuroraFlames.controller;

import com.ecomm.AuroraFlames.dto.CartDTO;
import com.ecomm.AuroraFlames.entity.Cart;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.service.CartService;
import com.ecomm.AuroraFlames.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal User user) {
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(dtoMapper.toCartDTO(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@AuthenticationPrincipal User user, 
                                          @RequestParam Long candleId, 
                                          @RequestParam Integer quantity) {
        Cart cart = cartService.addToCart(user, candleId, quantity);
        return ResponseEntity.ok(dtoMapper.toCartDTO(cart));
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(@AuthenticationPrincipal User user,
                                               @PathVariable Long itemId,
                                               @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartItem(user, itemId, quantity);
        return ResponseEntity.ok(dtoMapper.toCartDTO(cart));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartDTO> removeFromCart(@AuthenticationPrincipal User user,
                                               @PathVariable Long itemId) {
        Cart cart = cartService.removeFromCart(user, itemId);
        return ResponseEntity.ok(dtoMapper.toCartDTO(cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
}
