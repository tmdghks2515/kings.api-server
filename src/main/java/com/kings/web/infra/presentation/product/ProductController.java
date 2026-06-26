package com.kings.web.infra.presentation.product;

import com.kings.web.application.product.CreateProductCommand;
import com.kings.web.application.product.CreateProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductService createProductService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateProductCommand command) {
        createProductService.create(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
