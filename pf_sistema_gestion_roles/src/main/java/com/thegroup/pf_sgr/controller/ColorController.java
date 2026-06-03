package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.IColorService;
import com.thegroup.pf_sgr.model.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {

    private final IColorService colorService;

    @GetMapping
    public ResponseEntity<Page<Color>> getAllColors(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(colorService.getAllColors(page, size));
    }

    @GetMapping("/getColorById")
    public ResponseEntity<Color> getColorById(@RequestParam Integer colorId) {
        return ResponseEntity.ok(colorService.getColorById(colorId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createColor")
    public ResponseEntity<Color> createColor(@RequestBody Color color) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.saveColor(color));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateColor")
    public ResponseEntity<Color> updateColor(@RequestParam Integer colorId, @RequestBody Color color) {
        return ResponseEntity.ok(colorService.updateColor(colorId, color));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteColor")
    public ResponseEntity<Void> deleteColor(@RequestParam Integer colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.noContent().build();
    }
}
