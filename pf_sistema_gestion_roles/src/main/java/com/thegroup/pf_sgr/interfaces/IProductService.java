package com.thegroup.pf_sgr.interfaces;

import java.util.List;
import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Product;

public interface IProductService {
    Page<Product> getAllProductsPaginated(int page, int size, String sortDirection);
    Product getProductById(Integer productId);
    Product saveProduct(Product product);
    Product updateProduct(Integer productId, Product updatedProduct);
    void deactivateProduct(Integer productId);
    void activateProduct(Integer productId);
    void deleteProduct(Integer productId);
    List<Product> searchProductByName(String name);
}
