package com.kings.web.infra.data.jpa.product.category;

import com.kings.web.domain.category.Category;
import com.kings.web.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id);
    }

    @Override
    public List<Category> findByParentCategoryId(Long parentCategoryId) {
        return categoryJpaRepository.findByParentCategoryId(parentCategoryId);
    }

    @Override
    public void delete(Category category) {
        categoryJpaRepository.delete(category);
    }
}
