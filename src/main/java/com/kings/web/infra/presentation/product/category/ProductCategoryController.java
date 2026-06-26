package com.kings.web.infra.presentation.product.category;

import com.kings.web.application.product.category.ProductCategoryCommand;
import com.kings.web.application.product.category.ProductCategoryData;
import com.kings.web.application.product.category.ProductCategoryDeleteCommand;
import com.kings.web.application.product.category.ProductCategoryService;
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
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping
    public Long create(@RequestBody ProductCategoryCommand command) {
        return productCategoryService.create(command);
    }

    @GetMapping
    public List<ProductCategoryData> findAll() {
        return productCategoryService.findAll();
    }

    @GetMapping("/{id}")
    public ProductCategoryData findById(@PathVariable Long id) {
        return productCategoryService.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProductCategoryCommand command) {
        productCategoryService.update(id, command);
    }

    @PostMapping("/delete")
    public void deleteAll(@RequestBody ProductCategoryDeleteCommand command) {
        productCategoryService.deleteAll(command);
    }
}
