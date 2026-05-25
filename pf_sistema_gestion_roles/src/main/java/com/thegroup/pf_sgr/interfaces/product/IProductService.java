package com.thegroup.pf_sgr.interfaces.product;

import java.util.List;
import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Product;

public interface IProductService {
    Page<Product> getAllProductsPaginated(int page, int size, String sortDirection);
    Product getProductById(Integer id);
    Product saveProduct(Product product);
    Product updateProduct(Integer id, Product updatedProduct);
    void deactivateProduct(Integer id);
    void activateProduct(Integer id);
    void deleteProduct(Integer id);
    List<Product> searchProductByName(String name);
}
