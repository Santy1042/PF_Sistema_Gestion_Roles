package com.thegroup.pf_sgr.controller;

import org.springframework.web.bind.annotation.*;
import com.thegroup.pf_sgr.interfaces.IProductService;
import com.thegroup.pf_sgr.model.Product;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(productService.getAllProductsPaginated(page, size, sortDirection));
    }

    @GetMapping("/getProductById")
    public ResponseEntity<Product> getProductById(@RequestParam Integer productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping("/createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(product));
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(@RequestParam Integer productId, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(productId, updatedProduct));
    }

    @DeleteMapping("/deactivateProduct")
    public ResponseEntity<Void> deactivateProduct(@RequestParam Integer productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activateProduct")
    public ResponseEntity<Void> activateProduct(@RequestParam Integer productId) {
        productService.activateProduct(productId);
        return ResponseEntity.noContent().build();
    } 

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Void> deleteProduct(@RequestParam Integer productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchByName")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductByName(name);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }
}