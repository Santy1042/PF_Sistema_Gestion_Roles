package com.thegroup.pf_sgr.controller;

import com.thegroup.pf_sgr.interfaces.ICategoryService;
import com.thegroup.pf_sgr.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/getCategoryById")
    public ResponseEntity<Category> getCategoryById(@RequestParam Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/createCategory")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category));
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<Void> deleteCategory(@RequestParam Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}