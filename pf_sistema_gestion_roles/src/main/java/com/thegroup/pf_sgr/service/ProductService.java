package com.thegroup.pf_sgr.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.interfaces.IProductService;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements IProductService {
    
    private final ProductRepository productRepository;

    @Override
    public Page<Product> getAllProductsPaginated(int page, int size, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, "price");
        return productRepository.findAll(PageRequest.of(page, size, sort));
    }

    @Override
    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + productId));
    }

    @Override
    public Product saveProduct(Product product) {
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer productId, Product updatedProduct) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + productId));

        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setIsActive(updatedProduct.getIsActive());
        product.setIsFeatured(updatedProduct.getIsFeatured());
        product.setDiscountPercentage(updatedProduct.getDiscountPercentage());
        product.setCategory(updatedProduct.getCategory());
        
        return productRepository.save(product);
    }

    @Override
    public void deactivateProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + productId));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public void activateProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + productId));
        product.setIsActive(true);
        productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + productId));
        
        productRepository.delete(product);
    }

    @Override
    public List<Product> searchProductByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}