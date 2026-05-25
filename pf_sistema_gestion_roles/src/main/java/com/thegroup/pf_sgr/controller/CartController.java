package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ICartService;
import com.thegroup.pf_sgr.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping("/getCart")
    public ResponseEntity<?> getCart(@RequestParam Long userId) {
        try {
            Cart cart = cartService.getOrCreateCart(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/addItemToCart")
    public ResponseEntity<?> addItemToCart(
            @RequestParam Long userId,
            @RequestParam Integer productVariantId,
            @RequestParam Integer quantity) {
        try {
            Cart cart = cartService.addItemToCart(userId, productVariantId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/updateItemQuantity")
    public ResponseEntity<?> updateItemQuantity(
            @RequestParam Long cartId,
            @RequestParam Long itemCartId,
            @RequestParam Integer quantity) {
        try {
            Cart cart = cartService.updateItemQuantity(cartId, itemCartId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/removeItem")
    public ResponseEntity<?> removeItem(
            @RequestParam Long cartId,
            @RequestParam Long itemCartId) {
        try {
            Cart cart = cartService.removeItemFromCart(cartId, itemCartId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<?> clearCart(@RequestParam Long cartId) {
        try {
            cartService.clearCart(cartId);
            return ResponseEntity.ok(Map.of("message", "Carrito vaciado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/syncCart")
    public ResponseEntity<?> syncCart(
            @RequestParam Long userId,
            @RequestBody List<Map<String, Integer>> frontendItems) {
        try {
            Cart cart = cartService.syncCart(userId, frontendItems);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}