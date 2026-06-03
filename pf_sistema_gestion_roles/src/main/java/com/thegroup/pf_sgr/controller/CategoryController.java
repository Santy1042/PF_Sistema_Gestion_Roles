package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ICategoryService;
import com.thegroup.pf_sgr.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size));
    }

    @GetMapping("/getCategoryById")
    public ResponseEntity<Category> getCategoryById(@RequestParam Integer categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createCategory")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateCategory")
    public ResponseEntity<Category> updateCategory(@RequestParam Integer categoryId, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCategory")
    public ResponseEntity<Void> deleteCategory(@RequestParam Integer categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
