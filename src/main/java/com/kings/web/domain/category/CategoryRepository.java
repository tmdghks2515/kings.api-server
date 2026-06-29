package com.kings.web.domain.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);

    List<Category> findAll();

    Optional<Category> findById(Long id);

    List<Category> findByParentCategoryId(Long parentCategoryId);

    void delete(Category category);
}
