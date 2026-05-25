package com.thegroup.pf_sgr.interfaces.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Product;

public interface IProductService {
    Page<Product> getAllProductsPaginated(int page, int size);
    Optional<Product> getProductById(Integer id);
    Product saveProduct(Product product);
    Optional<Product> updateProduct(Integer id, Product updatedProduct);
    boolean deactivateProduct(Integer id);
    boolean activateProduct(Integer id);
    boolean deleteProduct(Integer id);
    List<Product> searchProductByName(String name);
}
