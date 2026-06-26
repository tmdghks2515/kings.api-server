package com.kings.web.application.product.category;

import com.kings.web.domain.product.category.ProductCategory;

import java.util.List;

public record ProductCategoryData(
        Long id,
        int depth,
        String name,
        Long parentCategoryId,
        List<ProductCategoryData> children
) {
    public static ProductCategoryData from(ProductCategory productCategory, List<ProductCategoryData> children) {
        var parentCategory = productCategory.getParentCategory();

        return new ProductCategoryData(
                productCategory.getId(),
                productCategory.getDepth(),
                productCategory.getName(),
                parentCategory == null ? null : parentCategory.getId(),
                children
        );
    }
}
