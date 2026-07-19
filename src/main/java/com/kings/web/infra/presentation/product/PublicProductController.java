package com.kings.web.infra.presentation.product;

import com.kings.web.application.product.ProductData;
import com.kings.web.application.product.ProductQuery;
import com.kings.web.application.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductData> findAll(@ModelAttribute ProductQuery query) {
        return productService.findAll(query);
    }

    @GetMapping("/{code}")
    public ProductData findByCode(@PathVariable String code) {
        return productService.findByCode(code);
    }
}
