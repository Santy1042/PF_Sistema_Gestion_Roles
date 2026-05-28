package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.dto.CartItemRequest;
import com.thegroup.pf_sgr.model.Cart;

import java.util.List;

public interface ICartService {
    Cart getOrCreateCart(Long userId);
    Cart addItemToCart(Long userId, Integer productVariantId, Integer quantity);
    Cart updateItemQuantity(Long userId, Long itemCartId, Integer quantity);
    Cart removeItemFromCart(Long userId, Long itemCartId);
    void clearCart(Long userId);
    Cart syncCart(Long userId, List<CartItemRequest> frontendItems);
}