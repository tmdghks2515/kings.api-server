package com.kings.web.domain.product;

public interface ProductRepository {
    boolean existsByCode(String code);

    Product save(Product product);
}
