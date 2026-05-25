package com.thegroup.pf_sgr.interfaces.product;

import java.util.List;
import java.util.Optional;

import com.thegroup.pf_sgr.model.Product;

public interface IProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Integer id);
    Product saveProduct(Product product);
    Optional<Product> updateProduct(Integer id, Product updatedProduct);
    boolean deactivateProduct(Integer id);
    boolean activateProduct(Integer id);
}
