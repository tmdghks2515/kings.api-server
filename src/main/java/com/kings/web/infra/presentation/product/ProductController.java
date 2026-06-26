package com.kings.web.infra.presentation.product;

import com.kings.web.application.product.CreateProductService;
import com.kings.web.application.product.ProductCommand;
import com.kings.web.application.product.ProductData;
import com.kings.web.application.product.ProductDeleteCommand;
import com.kings.web.application.product.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductService createProductService;
    private final ProductService productService;

    @PostMapping
    public void create(@RequestBody ProductCommand command) {
        createProductService.create(command);
    }

    @GetMapping
    public List<ProductData> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{code}")
    public ProductData findByCode(@PathVariable String code) {
        return productService.findByCode(code);
    }

    @PutMapping("/{code}")
    public void update(@PathVariable String code, @RequestBody ProductCommand command) {
        productService.update(code, command);
    }

    @PostMapping("/bulk-delete")
    public void deleteAll(@RequestBody ProductDeleteCommand command) {
        productService.deleteAll(command);
    }
}
