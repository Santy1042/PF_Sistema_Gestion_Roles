package com.thegroup.pf_sgr.interfaces.productVariant;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.ProductVariant;

public interface IProductVariantService {
    Page<ProductVariant> getAllVariantsPaginated(int page, int size, String sortDirection);
    ProductVariant getVariantById(Integer id);
    ProductVariant saveVariant(ProductVariant productVariant);
    ProductVariant updateVariant(Integer id, ProductVariant updatedVariant);
    void deactivateVariant(Integer id);
    void activateVariant(Integer id);
    void deleteVariant(Integer id);
}
