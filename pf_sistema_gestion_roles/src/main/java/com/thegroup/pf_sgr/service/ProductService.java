package com.thegroup.pf_sgr.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.product.IProductService;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    
    private final ProductRepository productRepository;

    @Override
    public Page<Product> getAllProductsPaginated(int page, int size, String sortDirection) {
        Sort sort = "asc".equalsIgnoreCase(sortDirection) ? Sort.by("price").ascending() : Sort.by("price").descending();
        return productRepository.findAll(PageRequest.of(page, size, sort));
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    @Override
    public Product saveProduct(Product product) {
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer id, Product updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));

        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setIsActive(updatedProduct.getIsActive());
        product.setIsFeatured(updatedProduct.getIsFeatured());
        product.setDiscountPercentage(updatedProduct.getDiscountPercentage());
        product.setCategory(updatedProduct.getCategory());
        product.setTags(updatedProduct.getTags());
        
        if (updatedProduct.getVariants() != null) {
            updatedProduct.getVariants().forEach(variant -> variant.setProduct(product));
            product.getVariants().clear();
            product.getVariants().addAll(updatedProduct.getVariants());
        }
        return productRepository.save(product);
    }

    @Override
    public void deactivateProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public void activateProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        product.setIsActive(true);
        productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con el ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProductByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}