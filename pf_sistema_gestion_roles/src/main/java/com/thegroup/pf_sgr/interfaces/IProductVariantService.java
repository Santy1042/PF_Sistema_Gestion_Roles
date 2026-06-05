package com.thegroup.pf_sgr.interfaces;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.ProductVariant;

public interface IProductVariantService {
    Page<ProductVariant> getAllVariantsPaginated(int page, int size, String sortDirection);
    ProductVariant getVariantById(Integer variantId);
    ProductVariant saveVariant(ProductVariant productVariant);
    ProductVariant updateVariant(Integer variantId, ProductVariant updatedVariant);
    void deactivateVariant(Integer variantId);
    void activateVariant(Integer variantId);
}
