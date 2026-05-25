package com.thegroup.pf_sgr.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.interfaces.product.IProductService;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    public Page<Product> getAllProductsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Integer id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setIsActive(updatedProduct.getIsActive());
            product.setIsFeatured(updatedProduct.getIsFeatured());
            product.setDiscountPercentage(updatedProduct.getDiscountPercentage());
            product.setCategory(updatedProduct.getCategory());
            product.setTags(updatedProduct.getTags());
            if (updatedProduct.getVariants() != null) {
                updatedProduct.getVariants().forEach(variant -> variant.setProduct(product));
                product.setVariants(updatedProduct.getVariants());
            }
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
    
    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Product> searchProductByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
