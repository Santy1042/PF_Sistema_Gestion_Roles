package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.interfaces.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<ProfileResponse>> listUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<ProfileResponse> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.changeRole(id, body));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteUser(id));
    }
}
