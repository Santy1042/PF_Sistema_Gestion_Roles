package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.UserRepository;
import jakarta.validation.Valid;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody Map<String, String> request) {
        String currentEmail = getCurrentEmail();

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (request.get("name") != null && !request.get("name").isEmpty()) {
            user.setName(request.get("name"));
        }

        if (request.get("email") != null && !request.get("email").isEmpty()
                && !request.get("email").equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.get("email"))) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }
            user.setEmail(request.get("email"));
        }

        if (request.get("password") != null && !request.get("password").isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.get("password")));
        }

        user = userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Perfil actualizado correctamente",
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount() {
        String currentEmail = getCurrentEmail();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        userRepository.deleteById(user.getId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Cuenta eliminada correctamente"
        ));
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        return auth.getName();
    }
}
    
