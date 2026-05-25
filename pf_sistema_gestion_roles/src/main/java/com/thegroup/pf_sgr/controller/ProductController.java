package com.thegroup.pf_sgr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thegroup.pf_sgr.interfaces.product.IProductService;
import com.thegroup.pf_sgr.model.Product;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/getProductById")
    public ResponseEntity<Product> getProductById(@RequestParam Integer id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        System.out.println("PRODUCTO RECIBIDO DEL FRONTEND: Nombre=" + product.getName() + " | Precio=" + product.getPrice());

        Product createdProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(@RequestParam Integer id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deactivateProduct")
    public ResponseEntity<Void> deactivateProduct(@RequestParam Integer id) {
        boolean deactivated = productService.deactivateProduct(id);
        if (deactivated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/activateProduct")
    public ResponseEntity<Void> activateProduct(@RequestParam Integer id) {
        boolean activated = productService.activateProduct(id);
        if (activated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    } 

}