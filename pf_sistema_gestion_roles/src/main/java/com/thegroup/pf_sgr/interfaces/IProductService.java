package com.thegroup.pf_sgr.interfaces;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.dto.ProductRequest;
import com.thegroup.pf_sgr.dto.ProductResponse;

public interface IProductService {
    Page<ProductResponse> getAllProductsPaginated(int page, int size, String sortDirection);
    ProductResponse getProductById(Integer productId);
    ProductResponse saveProduct(ProductRequest request);
    ProductResponse updateProduct(Integer productId, ProductRequest request);
    void deactivateProduct(Integer productId);
    void activateProduct(Integer productId);
    void deleteProduct(Integer productId);
    Page<ProductResponse> searchProductByName(String name, int page, int size);
}
