package com.thegroup.pf_sgr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.thegroup.pf_sgr.interfaces.productVariant.IProductVariantService;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductVariantService implements IProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    public List<ProductVariant> getAllProductVariants() {
        return productVariantRepository.findAll();
    }

    public Optional<ProductVariant> getProductVariantById(Integer id) {
        return productVariantRepository.findById(id);
    }

    public ProductVariant saveProductVariant(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    public Optional<ProductVariant> updateProductVariant(Integer id, ProductVariant updatedVariant) {
        return productVariantRepository.findById(id).map(variant -> {
            variant.setProduct(updatedVariant.getProduct());
            variant.setSize(updatedVariant.getSize());
            variant.setColor(updatedVariant.getColor());
            variant.setStock(updatedVariant.getStock());
            variant.setIsActive(updatedVariant.getIsActive());
            return productVariantRepository.save(variant);
        });
    }

    public boolean deactivateProductVariant(Integer id) {
        return productVariantRepository.findById(id).map(variant -> {
            variant.setIsActive(false);
            productVariantRepository.save(variant);
            return true;
        }).orElse(false);
    }

    public boolean activateProductVariant(Integer id) {
        return productVariantRepository.findById(id).map(variant -> {
            variant.setIsActive(true);
            productVariantRepository.save(variant);
            return true;
        }).orElse(false);
    }
}