package com.thegroup.pf_sgr.interfaces;

import org.springframework.data.domain.Page;
import com.thegroup.pf_sgr.model.Category;

public interface ICategoryService {
    Page<Category> getAllCategories(int page, int size);
    Category getCategoryById(Integer categoryId);
    Category saveCategory(Category category);
    void deleteCategory(Integer categoryId);
}