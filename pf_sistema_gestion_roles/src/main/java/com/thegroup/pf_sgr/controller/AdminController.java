package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.dto.ProfileResponse;
import com.thegroup.pf_sgr.dto.RoleChangeRequest;
import com.thegroup.pf_sgr.interfaces.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileResponse>> listUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PutMapping("/usuarios/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> changeRole(@PathVariable Long id, @Valid @RequestBody RoleChangeRequest request) {
        return ResponseEntity.ok(adminService.changeRole(id, request));
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteUser(id));
    }
}