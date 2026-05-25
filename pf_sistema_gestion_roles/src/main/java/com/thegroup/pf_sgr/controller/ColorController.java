package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.IColorService;
import com.thegroup.pf_sgr.model.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {

    private final IColorService colorService;

    @GetMapping("/getAllColors")
    public ResponseEntity<List<Color>> getAllColors() {
        return ResponseEntity.ok(colorService.getAllColors());
    }

    @GetMapping("/getColorById")
    public ResponseEntity<Color> getColorById(@RequestParam Integer colorId) {
        return ResponseEntity.ok(colorService.getColorById(colorId));
    }

    @PostMapping("/createColor")
    public ResponseEntity<Color> createColor(@RequestBody Color color) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.saveColor(color));
    }

    @DeleteMapping("/deleteColor")
    public ResponseEntity<Void> deleteColor(@RequestParam Integer colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.noContent().build();
    }
}