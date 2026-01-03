package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.entity.*;
import com.ecomm.AuroraFlames.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getCartByUser(User user) {
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCartItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
        // Force load cart items and their candles with images
        cart.getCartItems().forEach(item -> {
            item.getCandle().getImages().size();
        });
        return cart;
    }

    public Cart addToCart(User user, Long candleId, Integer quantity) {
        Cart cart = getCartByUser(user);
        Candle candle = candleRepository.findById(candleId)
                .orElseThrow(() -> new RuntimeException("Candle not found"));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getCandle().getId().equals(candleId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setCandle(candle);
            newItem.setQuantity(quantity);
            newItem.setPriceAtTime(candle.getPrice());
            cart.getCartItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart updateCartItem(User user, Long itemId, Integer quantity) {
        Cart cart = getCartByUser(user);
        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    public Cart removeFromCart(User user, Long itemId) {
        Cart cart = getCartByUser(user);
        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        return cartRepository.save(cart);
    }

    public void clearCart(User user) {
        Cart cart = getCartByUser(user);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
