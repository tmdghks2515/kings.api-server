package com.kings.web.infra.data.jpa.product;

import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public boolean existsByCode(String code) {
        return productJpaRepository.existsById(code);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }
}
