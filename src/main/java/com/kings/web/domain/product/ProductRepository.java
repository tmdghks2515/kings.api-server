package com.kings.web.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    boolean existsByCode(String code);

    Product save(Product product);

    List<Product> findAll();

    List<Product> findAll(String keyword, List<Long> categoryIds, Long brandId);

    List<Product> findAllByCodes(List<String> codes);

    List<ProductMainImageStorageKeyData> findMainImageStorageKeysByProductCodes(List<String> codes);

    List<ProductOptionNameData> findOptionNamesByProductCodes(List<String> codes);

    Optional<Product> findByCode(String code);

    void delete(Product product);

    long countByCodes(List<String> codes);

    void deleteAllByCodes(List<String> codes);
}
