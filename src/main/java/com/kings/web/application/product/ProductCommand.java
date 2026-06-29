package com.kings.web.application.product;

import com.kings.web.domain.product.option.ProductOptionType;

import java.util.List;

public record ProductCommand(
        String code,
        String name,
        Double price,
        Long categoryId,
        Long brandId,
        List<ProductOptionCommand> options,
        List<ProductImageCommand> images,
        List<Long> detailImages
) {
    public record ProductOptionCommand(
            String name,
            Double price,
            ProductOptionType type
    ) {
    }

    public record ProductImageCommand(
            Long fileResourceId,
            boolean main
    ) {
    }
}
