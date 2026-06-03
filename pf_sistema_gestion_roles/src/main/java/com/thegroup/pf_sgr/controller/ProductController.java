package com.thegroup.pf_sgr.controller;

import org.springframework.web.bind.annotation.*;
import com.thegroup.pf_sgr.interfaces.IProductService;
import com.thegroup.pf_sgr.dto.ProductRequest;
import com.thegroup.pf_sgr.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size, 
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(productService.getAllProductsPaginated(page, size, sortDirection));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> products = productService.searchProductByName(name, page, size);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(product));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer productId, 
            @Valid @RequestBody ProductRequest updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(productId, updatedProduct));
    }

    @PatchMapping("/{productId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Integer productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateProduct(@PathVariable Integer productId) {
        productService.activateProduct(productId);
        return ResponseEntity.noContent().build();
    } 

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
