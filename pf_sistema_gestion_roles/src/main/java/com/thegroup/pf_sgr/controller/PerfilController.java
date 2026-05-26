package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.IPerfilService;
import com.thegroup.pf_sgr.interfaces.PerfilRequest;
import com.thegroup.pf_sgr.interfaces.PerfilResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final IPerfilService perfilService;

    @GetMapping
    public ResponseEntity<PerfilResponse> getPerfil(Authentication authentication) {
        String correo = authentication.getName();
        return ResponseEntity.ok(perfilService.getPerfil(correo));
    }

    @PutMapping
    public ResponseEntity<PerfilResponse> updatePerfil(Authentication authentication, @RequestBody PerfilRequest request) {
        String correo = authentication.getName();
        return ResponseEntity.ok(perfilService.updatePerfil(correo, request));
    }
}
