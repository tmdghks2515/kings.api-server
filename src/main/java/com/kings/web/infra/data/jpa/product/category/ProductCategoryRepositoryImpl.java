package com.kings.web.infra.data.jpa.product.category;

import com.kings.web.domain.product.category.ProductCategory;
import com.kings.web.domain.product.category.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {

    private final ProductCategoryJpaRepository productCategoryJpaRepository;

    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return productCategoryJpaRepository.save(productCategory);
    }

    @Override
    public List<ProductCategory> findAll() {
        return productCategoryJpaRepository.findAll();
    }

    @Override
    public Optional<ProductCategory> findById(Long id) {
        return productCategoryJpaRepository.findById(id);
    }

    @Override
    public List<ProductCategory> findByParentCategoryId(Long parentCategoryId) {
        return productCategoryJpaRepository.findByParentCategoryId(parentCategoryId);
    }

    @Override
    public void delete(ProductCategory productCategory) {
        productCategoryJpaRepository.delete(productCategory);
    }
}
