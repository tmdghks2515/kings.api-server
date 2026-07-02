package com.kings.web.application.product;

public record ProductQuery(
        String keyword,
        Long categoryId,
        Long brandId
) {
}
