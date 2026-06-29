package com.kings.web.infra.presentation.product.category;

import com.kings.web.application.product.category.CategoryCommand;
import com.kings.web.application.product.category.CategoryData;
import com.kings.web.application.product.category.CategoryDeleteCommand;
import com.kings.web.application.product.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Long create(@RequestBody CategoryCommand command) {
        return categoryService.create(command);
    }

    @GetMapping
    public List<CategoryData> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryData findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody CategoryCommand command) {
        categoryService.update(id, command);
    }

    @PostMapping("/delete")
    public void deleteAll(@RequestBody CategoryDeleteCommand command) {
        categoryService.deleteAll(command);
    }
}
