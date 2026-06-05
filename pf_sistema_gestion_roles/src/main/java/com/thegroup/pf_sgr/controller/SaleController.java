package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.dto.CheckoutResponse;
import com.thegroup.pf_sgr.dto.SaleResponse;
import com.thegroup.pf_sgr.dto.StatusReportRequest;
import com.thegroup.pf_sgr.interfaces.ISaleService;
import com.thegroup.pf_sgr.repository.UserRepository;
import com.thegroup.pf_sgr.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final ISaleService saleService;
    private final UserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createOrder(Authentication authentication) {
        Long userId = AuthUtils.getUserId(authentication, userRepository);
        CheckoutResponse response = saleService.createOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<SaleResponse> confirmPayment(
            @PathVariable Long id,
            @RequestBody(required = false) StatusReportRequest report,
            Authentication authentication) {
        Long userId = AuthUtils.getUserId(authentication, userRepository);
        String status = report != null ? report.getStatusReport() : null;
        SaleResponse response = saleService.confirmPayment(id, userId, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SaleResponse> cancelOrder(
            @PathVariable Long id,
            @RequestBody(required = false) StatusReportRequest report,
            Authentication authentication) {
        Long userId = AuthUtils.getUserId(authentication, userRepository);
        String status = report != null ? report.getStatusReport() : null;
        SaleResponse response = saleService.cancelOrder(id, userId, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SaleResponse> refundOrder(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.refundOrder(id));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<SaleResponse>> getSalesByUser(Authentication authentication) {
        Long userId = AuthUtils.getUserId(authentication, userRepository);
        return ResponseEntity.ok(saleService.getSalesByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleDetail(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = AuthUtils.getUserId(authentication, userRepository);
        return ResponseEntity.ok(saleService.getSaleDetail(id, userId));
    }
}
