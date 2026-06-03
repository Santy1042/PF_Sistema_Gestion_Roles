package com.thegroup.pf_sgr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.IProductService;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.model.Tag;
import com.thegroup.pf_sgr.model.Category;
import com.thegroup.pf_sgr.dto.ProductRequest;
import com.thegroup.pf_sgr.dto.ProductResponse;
import com.thegroup.pf_sgr.dto.ProductVariantResponse;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.repository.ProductRepository;
import com.thegroup.pf_sgr.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .discountPercentage(product.getDiscountPercentage())
                .categoryId(product.getCategory() != null ? product.getCategory().getIdCategory() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .tags(product.getTags() != null ? 
                      product.getTags().stream().map(Tag::getName).toList() : 
                      new ArrayList<>())
                .variants(product.getVariants() != null ?
                      product.getVariants().stream().map(variant -> ProductVariantResponse.builder()
                          .id(variant.getVariantId())
                          .size(variant.getSize() != null ? variant.getSize().getName() : null)
                          .color(variant.getColor() != null ? variant.getColor().getName() : null)
                          .stock(variant.getStock())
                          .isActive(variant.getIsActive())
                          .imageUrl(variant.getImageUrl())
                          .build()
                      ).toList() :
                      new ArrayList<>())
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
        product.setCreatedAt(LocalDate.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.getReferenceById(request.getCategoryId());
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Integer productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (request.getName() != null && !request.getName().isEmpty()) product.setName(request.getName());
        if (request.getPrice() != null) product.setPrice(request.getPrice());

        if(request.getIsActive() != null) product.setIsActive(request.getIsActive());
        if(request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if(request.getDiscountPercentage() != null) product.setDiscountPercentage(request.getDiscountPercentage());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.getReferenceById(request.getCategoryId());
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
