package com.thegroup.pf_sgr.interfaces.productVariant;

import java.util.List;
import java.util.Optional;
import com.thegroup.pf_sgr.model.ProductVariant;

public interface IProductVariantService {
    List<ProductVariant> getAllProductVariants();
    Optional<ProductVariant> getProductVariantById(Integer id);
    ProductVariant saveProductVariant(ProductVariant productVariant);
    Optional<ProductVariant> updateProductVariant(Integer id, ProductVariant updatedVariant);
    boolean deactivateProductVariant(Integer id);
    boolean activateProductVariant(Integer id);
}
