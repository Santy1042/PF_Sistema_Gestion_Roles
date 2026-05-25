package com.thegroup.pf_sgr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thegroup.pf_sgr.interfaces.product.IProductService;
import com.thegroup.pf_sgr.model.Product;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import java.util.List;

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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Page<Product> products = productService.getAllProductsPaginated(page, size, sortDirection);
        return ResponseEntity.ok(products);
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

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Void> deleteProduct(@RequestParam Integer id) {
        boolean isDeleted = productService.deleteProduct(id);
        
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/searchByName")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductByName(name);
        
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no encuentra nada
        }
        
        return ResponseEntity.ok(products); // Retorna 200 con la lista
    }

}