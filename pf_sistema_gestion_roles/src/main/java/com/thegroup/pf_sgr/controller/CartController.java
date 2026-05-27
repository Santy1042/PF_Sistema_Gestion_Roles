package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ICartService;
import com.thegroup.pf_sgr.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @GetMapping("/getCart")
    public ResponseEntity<Cart> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/addItemToCart")
    public ResponseEntity<Cart> addItemToCart(@RequestParam Long userId, @RequestParam Integer productVariantId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, productVariantId, quantity));
    }

    @PutMapping("/updateItemQuantity")
    public ResponseEntity<Cart> updateItemQuantity(@RequestParam Long cartId, @RequestParam Long itemCartId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(cartId, itemCartId, quantity));
    }

    @DeleteMapping("/removeItemFromCart")
    public ResponseEntity<Cart> removeItem(@RequestParam Long cartId, @RequestParam Long itemCartId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(cartId, itemCartId));
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<Map<String, String>> clearCart(@RequestParam Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok(Map.of("message", "Carrito vaciado correctamente"));
    }

    @PostMapping("/syncCart")
    public ResponseEntity<Cart> syncCart( @RequestParam Long userId, @RequestBody List<Map<String, Integer>> frontendItems) {
        return ResponseEntity.ok(cartService.syncCart(userId, frontendItems));
    }
}