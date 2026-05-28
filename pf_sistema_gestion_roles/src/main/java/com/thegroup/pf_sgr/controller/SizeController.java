package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ISizeService;
import com.thegroup.pf_sgr.model.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final ISizeService sizeService;

    @GetMapping
    public ResponseEntity<Page<Size>> getAllSizes(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sizeService.getAllSizes(page, size));
    }

    @GetMapping("/getSizeById")
    public ResponseEntity<Size> getSizeById(@RequestParam Integer sizeId) {
        return ResponseEntity.ok(sizeService.getSizeById(sizeId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createSize")
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeService.saveSize(size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteSize")
    public ResponseEntity<Void> deleteSize(@RequestParam Integer sizeId) {
        sizeService.deleteSize(sizeId);
        return ResponseEntity.noContent().build();
    }
}