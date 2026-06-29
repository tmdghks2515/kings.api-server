package com.kings.web.application.product.category;

import com.kings.web.domain.category.Category;

import java.util.List;

public record CategoryData(
        Long id,
        int depth,
        String name,
        Long parentCategoryId,
        List<CategoryData> children
) {
    public static CategoryData from(Category category, List<CategoryData> children) {
        var parentCategory = category.getParentCategory();

        return new CategoryData(
                category.getId(),
                category.getDepth(),
                category.getName(),
                parentCategory == null ? null : parentCategory.getId(),
                children
        );
    }
}
