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
public class SizeController {

    private final ISizeService sizeService;

    @GetMapping("/getAllSizes")
    public ResponseEntity<List<Size>> getAllSizes() {
        return ResponseEntity.ok(sizeService.getAllSizes());
    }

    @GetMapping("/getSizeById")
    public ResponseEntity<Size> getSizeById(@RequestParam Integer id) {
        return sizeService.getSizeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createSize")
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        Size createdSize = sizeService.saveSize(size);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSize);
    }

    @DeleteMapping("/deleteSize")
    public ResponseEntity<Void> deleteSize(@RequestParam Integer id) {
        if (sizeService.deleteSize(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}