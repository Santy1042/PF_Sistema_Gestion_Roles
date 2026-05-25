package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.ICartService;
import com.thegroup.pf_sgr.model.Cart;
import com.thegroup.pf_sgr.model.CartItem;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.repository.CartRepository;
import com.thegroup.pf_sgr.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;

    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
            }

    @Override
    @Transactional
    public Cart addItemToCart(Long userId, Integer productVariantId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        
        ProductVariant variant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de producto no encontrada"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productVariantId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateItemQuantity(Long cartId, Long itemCartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getItemCartId().equals(itemCartId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado en el carrito"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(Long cartId, Long itemCartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        cart.getItems().removeIf(item -> item.getItemCartId().equals(itemCartId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart syncCart(Long userId, List<Map<String, Integer>> frontendItems) {
        Cart cart = getOrCreateCart(userId);
        
        for (Map<String, Integer> itemMap : frontendItems) {
            Integer variantId = itemMap.get("productVariantId");
            Integer quantity = itemMap.get("quantity");
            
            if (variantId != null && quantity != null) {
                ProductVariant variant = productVariantRepository.findById(variantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada: " + variantId));
                        
                Optional<CartItem> existingItem = cart.getItems().stream()
                        .filter(item -> item.getProductVariant().getId().equals(variantId))
                        .findFirst();
                        
                if (existingItem.isPresent()) {
                    existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProductVariant(variant);
                    newItem.setQuantity(quantity);
                    cart.getItems().add(newItem);
                }
            }
        }
        return cartRepository.save(cart);
    }
}