package com.thegroup.pf_sgr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thegroup.pf_sgr.interfaces.productVariant.IProductVariantService;
import com.thegroup.pf_sgr.model.ProductVariant;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final IProductVariantService variantService;

    @GetMapping("/getAllVariants")
    public ResponseEntity<Page<ProductVariant>> getAllVariants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Page<ProductVariant> variants = variantService.getAllVariantsPaginated(page, size, sortDirection);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/getVariantById")
    public ResponseEntity<ProductVariant> getVariantById(@RequestParam Integer id) {
        return variantService.getVariantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createVariant")
    public ResponseEntity<ProductVariant> createVariant(@RequestBody ProductVariant variant) {
        System.out.println("VARIANTE RECIBIDA DEL FRONTEND: Stock=" + variant.getStock() + " | Activa=" + variant.getIsActive());

        ProductVariant createdVariant = variantService.saveVariant(variant);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVariant);
    }

    @PutMapping("/updateVariant")
    public ResponseEntity<ProductVariant> updateVariant(@RequestParam Integer id, @RequestBody ProductVariant updatedVariant) {
        return variantService.updateVariant(id, updatedVariant)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deactivateVariant")
    public ResponseEntity<Void> deactivateVariant(@RequestParam Integer id) {
        boolean deactivated = variantService.deactivateVariant(id);
        if (deactivated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/activateVariant")
    public ResponseEntity<Void> activateVariant(@RequestParam Integer id) {
        boolean activated = variantService.activateVariant(id);
        if (activated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    } 

    @DeleteMapping("/deleteVariant")
    public ResponseEntity<Void> deleteVariant(@RequestParam Integer id) {
        boolean isDeleted = variantService.deleteVariant(id);
        
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}