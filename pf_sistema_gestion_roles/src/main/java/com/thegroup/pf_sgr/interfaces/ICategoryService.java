package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.model.Category;
import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Integer categoryId);
    Category saveCategory(Category category);
    void deleteCategory(Integer categoryId);
}