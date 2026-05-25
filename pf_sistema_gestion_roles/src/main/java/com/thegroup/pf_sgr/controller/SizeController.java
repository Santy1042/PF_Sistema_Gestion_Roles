package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ISizeService;
import com.thegroup.pf_sgr.model.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SizeController {

    private final ISizeService sizeService;

    @GetMapping("/getAllSizes")
    public ResponseEntity<List<Size>> getAllSizes() {
        return ResponseEntity.ok(sizeService.getAllSizes());
    }

    @GetMapping("/getSizeById")
    public ResponseEntity<Size> getSizeById(@RequestParam Integer id) {
        return ResponseEntity.ok(sizeService.getSizeById(id));
    }

    @PostMapping("/createSize")
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeService.saveSize(size));
    }

    @DeleteMapping("/deleteSize")
    public ResponseEntity<Void> deleteSize(@RequestParam Integer id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}