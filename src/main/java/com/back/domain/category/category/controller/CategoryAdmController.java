package com.back.domain.category.category.controller;

import com.back.domain.category.category.dto.CategoryCreateReqBody;
import com.back.domain.category.category.dto.CategoryResBody;
import com.back.domain.category.category.dto.CategoryUpdateReqBody;
import com.back.domain.category.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/adm/categories")
public class CategoryAdmController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResBody> createCategory(@Valid @RequestBody CategoryCreateReqBody categoryCreateReqBody) {
        CategoryResBody categoryResBody = categoryService.createCategory(categoryCreateReqBody);
        return ResponseEntity.ok(categoryResBody);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResBody> updateCategory(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody CategoryUpdateReqBody categoryUpdateReqBody) {
        CategoryResBody categoryResBody = categoryService.updateCategory(categoryId, categoryUpdateReqBody);
        return ResponseEntity.ok(categoryResBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }
}
