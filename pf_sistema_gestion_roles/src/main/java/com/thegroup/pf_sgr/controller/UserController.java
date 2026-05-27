package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.repository.UsuarioRepository;

import jakarta.validation.Valid;

import com.thegroup.pf_sgr.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody Map<String, String> request) {
    String correoActual = obtenerCorreoActual();

    Usuario usuario = userRepository.findByCorreo(correoActual)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    if (request.get("nombre") != null && !request.get("nombre").isEmpty()) {
        usuario.setNombre(request.get("nombre"));
    }

    if (request.get("correo") != null && !request.get("correo").isEmpty()
            && !request.get("correo").equals(usuario.getCorreo())) {
        if (userRepository.existsByCorreo(request.get("correo"))) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        usuario.setCorreo(request.get("correo"));
    }

    if (request.get("contrasena") != null && !request.get("contrasena").isEmpty()) {
        usuario.setContrasena(passwordEncoder.encode(request.get("contrasena")));
    }

    usuario = userRepository.save(usuario);

    return ResponseEntity.ok(Map.of(
            "mensaje", "Perfil actualizado correctamente",
            "id", usuario.getId(),
            "nombre", usuario.getNombre(),
            "correo", usuario.getCorreo(),
            "rol", usuario.getRol()
        ));
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount() {
        String correoActual = obtenerCorreoActual();
        Usuario usuario = userRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        userRepository.deleteById(usuario.getId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Cuenta eliminada correctamente"
        ));
    }

    private String obtenerCorreoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        return auth.getName();
    }
}
    
