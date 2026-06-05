package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.dto.ProfileResponse;
import com.thegroup.pf_sgr.dto.RoleChangeRequest;
import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.interfaces.ISaleService;
import com.thegroup.pf_sgr.dto.DashboardStatsResponse;
import com.thegroup.pf_sgr.dto.SaleResponse;
import com.thegroup.pf_sgr.dto.AdminSaleUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.thegroup.pf_sgr.dto.AdminUserUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;
    private final ISaleService saleService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @PutMapping("/sales/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SaleResponse> updateSaleAdmin(
            @PathVariable Long id, 
            @Valid @RequestBody AdminSaleUpdateRequest request) {
        return ResponseEntity.ok(saleService.updateSaleAdmin(id, request));
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileResponse>> listUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @GetMapping("/usuarios/buscar/nombre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileResponse>> searchUsersByName(
        @RequestParam String nombre) {
        return ResponseEntity.ok(
            adminService.searchUsersByName(nombre)
        );
    }

    @GetMapping("/usuarios/buscar/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileResponse>> searchUsersByEmail(
        @RequestParam String email) {

        return ResponseEntity.ok(
            adminService.searchUsersByEmail(email)
        );
    }

    @PutMapping("/usuarios/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> changeRole(@PathVariable Long id, @Valid @RequestBody RoleChangeRequest request) {
        return ResponseEntity.ok(adminService.changeRole(id, request));
    }

    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteUser(id));
    }
}
