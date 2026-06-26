package com.kings.web.domain.product.category;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository {
    ProductCategory save(ProductCategory productCategory);

    List<ProductCategory> findAll();

    Optional<ProductCategory> findById(Long id);

    List<ProductCategory> findByParentCategoryId(Long parentCategoryId);

    void delete(ProductCategory productCategory);
}
