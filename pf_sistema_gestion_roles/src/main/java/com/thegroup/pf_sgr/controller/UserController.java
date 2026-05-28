package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        String email = authentication.getName();
        
        userService.deleteAccount(email);

        return ResponseEntity.ok(Map.of(
                "message", "Cuenta eliminada correctamente"
        ));
    }
}