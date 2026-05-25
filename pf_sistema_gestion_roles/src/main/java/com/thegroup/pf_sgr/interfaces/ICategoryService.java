package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Category;
import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Integer id);
    Category saveCategory(Category category);
    boolean deleteCategory(Integer id);
}