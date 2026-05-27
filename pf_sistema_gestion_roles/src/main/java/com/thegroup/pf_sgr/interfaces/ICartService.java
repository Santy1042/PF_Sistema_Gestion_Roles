package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Cart;
import java.util.List;
import java.util.Map;

public interface ICartService {
    Cart getOrCreateCart(Long userId);
    Cart addItemToCart(Long userId, Integer productVariantId, Integer quantity);
    Cart updateItemQuantity(Long cartId, Long itemCartId, Integer quantity);
    Cart removeItemFromCart(Long cartId, Long itemCartId);
    void clearCart(Long cartId);
    Cart syncCart(Long userId, List<Map<String, Integer>> frontendItems);
}