package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.dto.CartItemRequest;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
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
    public Cart addItemToCart(Long userId, Integer productVariantId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        
        ProductVariant variant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de producto no encontrada"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getVariantId().equals(productVariantId))
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
    public Cart updateItemQuantity(Long userId, Long itemCartId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getItemCartId().equals(itemCartId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado en tu carrito"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long itemCartId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getItemCartId().equals(itemCartId));
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public Cart syncCart(Long userId, List<CartItemRequest> frontendItems) {
        Cart cart = getOrCreateCart(userId);
        
        List<Integer> variantIds = frontendItems.stream()
                .map(CartItemRequest::getProductVariantId)
                .toList();

        List<ProductVariant> variants = productVariantRepository.findAllById(variantIds);
        
        Map<Integer, ProductVariant> variantMap = variants.stream()
                .collect(Collectors.toMap(ProductVariant::getVariantId, v -> v));

        for (CartItemRequest request : frontendItems) {
            ProductVariant variant = variantMap.get(request.getProductVariantId());
            if (variant == null) continue;
                        
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductVariant().getVariantId().equals(variant.getVariantId()))
                    .findFirst();
                    
            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProductVariant(variant);
                newItem.setQuantity(request.getQuantity());
                cart.getItems().add(newItem);
            }
        }
        
        return cartRepository.save(cart);
    }
}