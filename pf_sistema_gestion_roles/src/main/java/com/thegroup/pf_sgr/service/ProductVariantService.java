package com.thegroup.pf_sgr.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.interfaces.IProductVariantService;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductVariantService implements IProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    public Page<ProductVariant> getAllVariantsPaginated(int page, int size, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, "stock");
        return productVariantRepository.findAll(PageRequest.of(page, size, sort));
    }

    @Override
    public ProductVariant getVariantById(Integer variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variante no encontrada con el ID: " + variantId));
    }

    @Override
    public ProductVariant saveVariant(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    @Override
    public ProductVariant updateVariant(Integer variantId, ProductVariant updatedVariant) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variante no encontrada con el ID: " + variantId));

        if (updatedVariant.getProduct() != null) variant.setProduct(updatedVariant.getProduct());
        if (updatedVariant.getSize() != null) variant.setSize(updatedVariant.getSize());
        if (updatedVariant.getColor() != null) variant.setColor(updatedVariant.getColor());
        if (updatedVariant.getImageUrl() != null) variant.setImageUrl(updatedVariant.getImageUrl());
        
        variant.setStock(updatedVariant.getStock());
        variant.setIsActive(updatedVariant.getIsActive());
        
        return productVariantRepository.save(variant);
    }

    @Override
    public void deactivateVariant(Integer variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variante no encontrada con el ID: " + variantId));
        variant.setIsActive(false);
        productVariantRepository.save(variant);
    }

    @Override
    public void activateVariant(Integer variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variante no encontrada con el ID: " + variantId));
        variant.setIsActive(true);
        productVariantRepository.save(variant);
    }

    @Override
    public void deleteVariant(Integer variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variante no encontrada con el ID: " + variantId));
        productVariantRepository.delete(variant);
    }
}