package com.kings.web.application.product.category;

import java.util.List;

public record CategoryCommand(
        Long id,
        int depth,
        String name,
        Long parentCategoryId,
        List<CategoryCommand> children
) {
}
