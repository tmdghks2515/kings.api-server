package com.kings.web.infra.data.jpa.product.category;

import com.kings.web.domain.product.category.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryJpaRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByParentCategoryId(Long parentCategoryId);
}
