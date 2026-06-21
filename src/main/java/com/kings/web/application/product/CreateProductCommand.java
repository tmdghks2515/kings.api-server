package com.kings.web.application.product;

import com.kings.web.domain.product.option.ProductOptionType;

import java.util.List;

public record CreateProductCommand(
        String code,
        String name,
        Double price,
        List<CreateProductOptionCommand> options
) {
    public record CreateProductOptionCommand(
            String name,
            Double price,
            ProductOptionType type
    ) {
    }
}
