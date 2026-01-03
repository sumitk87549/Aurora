package com.ecomm.AuroraFlames.util;

import com.ecomm.AuroraFlames.dto.CandleDTO;
import com.ecomm.AuroraFlames.dto.CandleImageDTO;
import com.ecomm.AuroraFlames.dto.CartDTO;
import com.ecomm.AuroraFlames.dto.CartItemDTO;
import com.ecomm.AuroraFlames.dto.OrderDTO;
import com.ecomm.AuroraFlames.dto.OrderItemDTO;
import com.ecomm.AuroraFlames.dto.WishlistDTO;
import com.ecomm.AuroraFlames.dto.WishlistItemDTO;
import com.ecomm.AuroraFlames.entity.Candle;
import com.ecomm.AuroraFlames.entity.CandleImage;
import com.ecomm.AuroraFlames.entity.Cart;
import com.ecomm.AuroraFlames.entity.CartItem;
import com.ecomm.AuroraFlames.entity.Order;
import com.ecomm.AuroraFlames.entity.OrderItem;
import com.ecomm.AuroraFlames.entity.Wishlist;
import com.ecomm.AuroraFlames.entity.WishlistItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOMapper {

    public CandleDTO toCandleDTO(Candle candle) {
        CandleDTO dto = new CandleDTO();
        dto.setId(candle.getId());
        dto.setName(candle.getName());
        dto.setDescription(candle.getDescription());
        dto.setPrice(candle.getPrice());
        dto.setStockQuantity(candle.getStockQuantity());
        dto.setAvailable(candle.isAvailable());
        dto.setCreatorsChoice(candle.getCreatorsChoice());
        dto.setCreatorsText(candle.getCreatorsText());
        dto.setFeatured(candle.getFeatured());
        dto.setCategory(candle.getCategory());
        dto.setFragrance(candle.getFragrance());
        dto.setColor(candle.getColor());

        if (candle.getImages() != null) {
            List<CandleImageDTO> imageDTOs = candle.getImages().stream()
                    .map(this::toCandleImageDTO)
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        return dto;
    }

    public CandleImageDTO toCandleImageDTO(CandleImage image) {
        CandleImageDTO dto = new CandleImageDTO();
        dto.setId(image.getId());
        dto.setImageName(image.getImageName());
        dto.setImageUrl(image.getImageUrl());
        dto.setContentType(image.getContentType());

        // Don't send imageData in DTO - images should be accessed via URL
        // This prevents large payloads and performance issues

        return dto;
    }

    public List<CandleDTO> toCandleDTOList(List<Candle> candles) {
        return candles.stream()
                .map(this::toCandleDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingCost(order.getShippingCost());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setOrderDate(order.getOrderDate());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setOrderNotes(order.getOrderNotes());
        dto.setAdminNotes(order.getAdminNotes());

        if (order.getOrderItems() != null) {
            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(this::toOrderItemDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(orderItemDTOs);
        }

        return dto;
    }

    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setCandle(toCandleDTO(orderItem.getCandle()));
        dto.setQuantity(orderItem.getQuantity());
        dto.setPriceAtTime(orderItem.getPriceAtTime());
        return dto;
    }

    public List<OrderDTO> toOrderDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());
    }

    public WishlistDTO toWishlistDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());

        if (wishlist.getWishlistItems() != null) {
            List<WishlistItemDTO> wishlistItemDTOs = wishlist.getWishlistItems().stream()
                    .map(this::toWishlistItemDTO)
                    .collect(Collectors.toList());
            dto.setWishlistItems(wishlistItemDTOs);
        }

        return dto;
    }

    public WishlistItemDTO toWishlistItemDTO(WishlistItem wishlistItem) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setId(wishlistItem.getId());
        dto.setCandle(toCandleDTO(wishlistItem.getCandle()));
        dto.setAddedAt(wishlistItem.getAddedAt());
        return dto;
    }

    public List<WishlistDTO> toWishlistDTOList(List<Wishlist> wishlists) {
        return wishlists.stream()
                .map(this::toWishlistDTO)
                .collect(Collectors.toList());
    }

    public CartDTO toCartDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        if (cart.getCartItems() != null) {
            List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                    .map(this::toCartItemDTO)
                    .collect(Collectors.toList());
            dto.setCartItems(cartItemDTOs);
        }

        return dto;
    }

    public CartItemDTO toCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setCandle(toCandleDTO(cartItem.getCandle()));
        dto.setQuantity(cartItem.getQuantity());
        dto.setPriceAtTime(cartItem.getPriceAtTime());
        return dto;
    }
}
