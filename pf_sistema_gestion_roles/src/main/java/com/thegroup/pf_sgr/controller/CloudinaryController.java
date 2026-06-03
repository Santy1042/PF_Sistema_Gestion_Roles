package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ICloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class CloudinaryController {

    private final ICloudinaryService cloudinaryService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file);
        return ResponseEntity.ok(Map.of("url", imageUrl));
    }
}
