package com.thegroup.pf_sgr.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
        Sort sort;
        if ("asc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by("stock").ascending();
        } else {
            sort = Sort.by("stock").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return productVariantRepository.findAll(pageable);
    }

    @Override
    public Optional<ProductVariant> getVariantById(Integer id) {
        return productVariantRepository.findById(id);
    }

    @Override
    public ProductVariant saveVariant(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    @Override
    public Optional<ProductVariant> updateVariant(Integer id, ProductVariant updatedVariant) {
        return productVariantRepository.findById(id).map(variant -> {
            if (updatedVariant.getProduct() != null) {
                variant.setProduct(updatedVariant.getProduct());
            }
            if (updatedVariant.getSize() != null) {
                variant.setSize(updatedVariant.getSize());
            }
            if (updatedVariant.getColor() != null) {
                variant.setColor(updatedVariant.getColor());
            }
            if (updatedVariant.getImageUrl() != null) {
                variant.setImageUrl(updatedVariant.getImageUrl());
            }
            
            variant.setStock(updatedVariant.getStock());
            variant.setIsActive(updatedVariant.getIsActive());
            
            return productVariantRepository.save(variant);
        });
    }

    @Override
    public boolean deactivateVariant(Integer id) {
        return productVariantRepository.findById(id).map(variant -> {
            variant.setIsActive(false);
            productVariantRepository.save(variant);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean activateVariant(Integer id) {
        return productVariantRepository.findById(id).map(variant -> {
            variant.setIsActive(true);
            productVariantRepository.save(variant);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean deleteVariant(Integer id) {
        if (productVariantRepository.existsById(id)) {
            productVariantRepository.deleteById(id);
            return true;
        }
        return false;
    }
}