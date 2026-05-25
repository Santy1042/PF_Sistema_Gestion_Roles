package com.thegroup.pf_sgr.interfaces.productVariant;

import java.util.Optional;
import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.ProductVariant;

public interface IProductVariantService {
    Page<ProductVariant> getAllVariantsPaginated(int page, int size, String sortDirection);
    Optional<ProductVariant> getVariantById(Integer id);
    ProductVariant saveVariant(ProductVariant productVariant);
    Optional<ProductVariant> updateVariant(Integer id, ProductVariant updatedVariant);
    boolean deactivateVariant(Integer id);
    boolean activateVariant(Integer id);
    boolean deleteVariant(Integer id);
}
