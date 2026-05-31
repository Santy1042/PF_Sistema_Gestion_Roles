package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.dto.CheckoutResponseDTO;
import com.thegroup.pf_sgr.dto.SaleResponseDTO;
import com.thegroup.pf_sgr.interfaces.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private ISaleService saleService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> createOrder(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        CheckoutResponseDTO response = saleService.createOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<SaleResponseDTO> confirmPayment(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        SaleResponseDTO response = saleService.confirmPayment(id, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelOrder(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        SaleResponseDTO response = saleService.cancelOrder(id, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SaleResponseDTO> refundOrder(@PathVariable Long id) {
        SaleResponseDTO response = saleService.refundOrder(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByUser(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<SaleResponseDTO> response = saleService.getSalesByUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleDetail(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        SaleResponseDTO response = saleService.getSaleDetail(id, userId);
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Extract user ID from authentication principal
        // This assumes the principal contains user ID information
        // Adjust based on your security configuration
        return Long.parseLong(authentication.getName());
    }
}
