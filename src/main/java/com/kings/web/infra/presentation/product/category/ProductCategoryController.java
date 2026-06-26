package com.kings.web.infra.presentation.product.category;

import com.kings.web.application.product.category.ProductCategoryCommand;
import com.kings.web.application.product.category.ProductCategoryData;
import com.kings.web.application.product.category.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<Long> create(@RequestBody ProductCategoryCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productCategoryService.create(command));
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
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ProductCategoryCommand command) {
        productCategoryService.update(id, command);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productCategoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
