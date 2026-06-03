package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.dto.ProfileRequest;
import com.thegroup.pf_sgr.dto.ProfileResponse;
import com.thegroup.pf_sgr.interfaces.IProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.getProfile(email));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(Authentication authentication, @Valid @RequestBody ProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateProfile(email, request));
    }
}
