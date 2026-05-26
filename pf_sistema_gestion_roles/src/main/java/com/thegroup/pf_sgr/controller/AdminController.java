package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.IAdminService;
import com.thegroup.pf_sgr.interfaces.PerfilResponse;
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
    public ResponseEntity<List<PerfilResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<PerfilResponse> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.cambiarRol(id, body));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.eliminarUsuario(id));
    }
}
