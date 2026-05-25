package com.thegroup.pf_sgr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.exception.ResourceNotFoundException;
import com.thegroup.pf_sgr.interfaces.productVariant.IProductVariantService;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductVariantService implements IProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    public Page<ProductVariant> getAllVariantsPaginated(int page, int size, String sortDirection) {
        Sort sort = "asc".equalsIgnoreCase(sortDirection) ? Sort.by("stock").ascending() : Sort.by("stock").descending();
        return productVariantRepository.findAll(PageRequest.of(page, size, sort));
    }

    @Override
    public ProductVariant getVariantById(Integer id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con el ID: " + id));
    }

    @Override
    public ProductVariant saveVariant(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    @Override
    public ProductVariant updateVariant(Integer id, ProductVariant updatedVariant) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con el ID: " + id));

        if (updatedVariant.getProduct() != null) variant.setProduct(updatedVariant.getProduct());
        if (updatedVariant.getSize() != null) variant.setSize(updatedVariant.getSize());
        if (updatedVariant.getColor() != null) variant.setColor(updatedVariant.getColor());
        if (updatedVariant.getImageUrl() != null) variant.setImageUrl(updatedVariant.getImageUrl());
        
        variant.setStock(updatedVariant.getStock());
        variant.setIsActive(updatedVariant.getIsActive());
        
        return productVariantRepository.save(variant);
    }

    @Override
    public void deactivateVariant(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con el ID: " + id));
        variant.setIsActive(false);
        productVariantRepository.save(variant);
    }

    @Override
    public void activateVariant(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con el ID: " + id));
        variant.setIsActive(true);
        productVariantRepository.save(variant);
    }

    @Override
    public void deleteVariant(Integer id) {
        if (!productVariantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Variante no encontrada con el ID: " + id);
        }
        productVariantRepository.deleteById(id);
    }
}