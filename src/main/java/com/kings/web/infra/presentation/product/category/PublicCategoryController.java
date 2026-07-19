package com.kings.web.infra.presentation.product.category;

import com.kings.web.application.category.CategoryData;
import com.kings.web.application.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/product-categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryData> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryData findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }
}
