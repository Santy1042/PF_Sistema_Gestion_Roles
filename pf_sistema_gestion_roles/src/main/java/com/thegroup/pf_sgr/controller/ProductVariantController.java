package com.thegroup.pf_sgr.controller;

import org.springframework.web.bind.annotation.*;
import com.thegroup.pf_sgr.interfaces.productVariant.IProductVariantService;
import com.thegroup.pf_sgr.model.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final IProductVariantService variantService;

    @GetMapping("/getAllVariants")
    public ResponseEntity<Page<ProductVariant>> getAllVariants(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(variantService.getAllVariantsPaginated(page, size, sortDirection));
    }

    @GetMapping("/getVariantById")
    public ResponseEntity<ProductVariant> getVariantById(@RequestParam Integer id) {
        return ResponseEntity.ok(variantService.getVariantById(id));
    }

    @PostMapping("/createVariant")
    public ResponseEntity<ProductVariant> createVariant(@RequestBody ProductVariant variant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(variantService.saveVariant(variant));
    }

    @PutMapping("/updateVariant")
    public ResponseEntity<ProductVariant> updateVariant(@RequestParam Integer id, @RequestBody ProductVariant updatedVariant) {
        return ResponseEntity.ok(variantService.updateVariant(id, updatedVariant));
    }

    @DeleteMapping("/deactivateVariant")
    public ResponseEntity<Void> deactivateVariant(@RequestParam Integer id) {
        variantService.deactivateVariant(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activateVariant")
    public ResponseEntity<Void> activateVariant(@RequestParam Integer id) {
        variantService.activateVariant(id);
        return ResponseEntity.noContent().build();
    } 

    @DeleteMapping("/deleteVariant")
    public ResponseEntity<Void> deleteVariant(@RequestParam Integer id) {
        variantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }
}