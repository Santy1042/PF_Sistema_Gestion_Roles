package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.dto.CartItemRequest;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.ICartService;
import com.thegroup.pf_sgr.model.Cart;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;
    private final UserRepository userRepository;

    private Long getAuthenticatedUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return user.getIdUser(); 
    }

    @GetMapping("/getCart")
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/addItemToCart")
    public ResponseEntity<Cart> addItemToCart(Authentication authentication, @RequestParam Integer productVariantId, @RequestParam Integer quantity) {
        Long userId = getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(cartService.addItemToCart(userId, productVariantId, quantity));
    }

    @PutMapping("/updateItemQuantity")
    public ResponseEntity<Cart> updateItemQuantity(Authentication authentication, @RequestParam Long itemCartId, @RequestParam Integer quantity) {
        Long userId = getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemCartId, quantity));
    }

    @DeleteMapping("/removeItemFromCart")
    public ResponseEntity<Cart> removeItem(Authentication authentication, @RequestParam Long itemCartId) {
        Long userId = getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemCartId));
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<Map<String, String>> clearCart(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "Carrito vaciado correctamente"));
    }

    @PostMapping("/syncCart")
    public ResponseEntity<Cart> syncCart(Authentication authentication, @Valid @RequestBody List<CartItemRequest> frontendItems) {
        Long userId = getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(cartService.syncCart(userId, frontendItems));
    }
}