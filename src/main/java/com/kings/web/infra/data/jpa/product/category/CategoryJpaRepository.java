package com.kings.web.infra.data.jpa.product.category;

import com.kings.web.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentCategoryId(Long parentCategoryId);
}
