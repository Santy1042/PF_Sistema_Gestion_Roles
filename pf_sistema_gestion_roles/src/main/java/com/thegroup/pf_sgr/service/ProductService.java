package com.thegroup.pf_sgr.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.thegroup.pf_sgr.interfaces.product.IProductRepository;
import com.thegroup.pf_sgr.interfaces.product.IProductService;
import com.thegroup.pf_sgr.model.Product;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Integer id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setIsActive(updatedProduct.getIsActive());
            product.setVariants(updatedProduct.getVariants());
            return productRepository.save(product);
        });
    }

    public boolean deactivateProduct(Integer id) {
        return productRepository.findById(id).map(product -> {
            product.setIsActive(false);
            productRepository.save(product);
            return true;
        }).orElse(false);
    }

    public boolean activateProduct(Integer id) {
    return productRepository.findById(id).map(product -> {
            product.setIsActive(true);
            productRepository.save(product);
            return true;
        }).orElse(false);
    }
}
