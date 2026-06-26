package com.kings.web.application.product.category;

import java.util.List;

public record ProductCategoryCommand(
        Long id,
        int depth,
        String name,
        Long parentCategoryId,
        List<ProductCategoryCommand> children
) {
}
