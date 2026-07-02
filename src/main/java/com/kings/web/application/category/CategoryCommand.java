package com.kings.web.application.category;

import java.util.List;

public record CategoryCommand(
        Long id,
        int depth,
        String name,
        int sortOrder,
        Long parentCategoryId,
        List<CategoryCommand> children
) {
}
