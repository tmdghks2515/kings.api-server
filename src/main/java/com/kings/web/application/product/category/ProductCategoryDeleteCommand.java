package com.kings.web.application.product.category;

import java.util.List;

public record ProductCategoryDeleteCommand(
        List<Long> categoryIds
) {
}
