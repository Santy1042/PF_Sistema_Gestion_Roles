package com.thegroup.pf_sgr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.IProductService;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.model.Category;
import com.thegroup.pf_sgr.dto.ProductRequest;
import com.thegroup.pf_sgr.dto.ProductResponse;
import com.thegroup.pf_sgr.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements IProductService {
    
    private final ProductRepository productRepository;

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .discountPercentage(product.getDiscountPercentage())
                .build();
    }

    @Override
    public Page<ProductResponse> getAllProductsPaginated(int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "price");
        return productRepository.findAll(PageRequest.of(page, size, sort))
                .map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + productId));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse saveProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        product.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        product.setDiscountPercentage(request.getDiscountPercentage() != null ? request.getDiscountPercentage() : 0);
        product.setCreatedAt(LocalDate.now()); // Fecha de creación automática
        
        // Manejo seguro de la Categoría
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setIdCategory(request.getCategoryId());
            product.setCategory(category);
        }
        
        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Integer productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        
        if(request.getIsActive() != null) product.setIsActive(request.getIsActive());
        if(request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if(request.getDiscountPercentage() != null) product.setDiscountPercentage(request.getDiscountPercentage());
        
        // Manejo seguro de la Categoría al actualizar
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setIdCategory(request.getCategoryId());
            product.setCategory(category);
        }
        
        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deactivateProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public void activateProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        product.setIsActive(true);
        productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productRepository.delete(product);
    }

    @Override
    public Page<ProductResponse> searchProductByName(String name, int page, int size) {
        return productRepository.findByNameContainingIgnoreCase(name, PageRequest.of(page, size))
                .map(this::mapToResponse);
    }
}