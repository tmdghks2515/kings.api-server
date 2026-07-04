package com.kings.web.infra.data.jpa.product;

import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.ProductMainImageStorageKeyData;
import com.kings.web.domain.product.ProductOptionNameData;
import com.kings.web.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public List<Product> findAll(String keyword, Long categoryId, Long brandId) {
        return productJpaRepository.search(keyword, categoryId, brandId);
    }

    @Override
    public List<Product> findAllByCodes(List<String> codes) {
        return productJpaRepository.findByCodeIn(codes);
    }

    @Override
    public List<ProductMainImageStorageKeyData> findMainImageStorageKeysByProductCodes(List<String> codes) {
        return productJpaRepository.findMainImageStorageKeysByProductCodeIn(codes);
    }

    @Override
    public List<ProductOptionNameData> findOptionNamesByProductCodes(List<String> codes) {
        return productJpaRepository.findOptionNamesByProductCodeIn(codes);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return productJpaRepository.findById(code);
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }

    @Override
    public long countByCodes(List<String> codes) {
        return productJpaRepository.countByCodeIn(codes);
    }

    @Override
    public void deleteAllByCodes(List<String> codes) {
        productJpaRepository.deleteOptionsByProductCodeIn(codes);
        productJpaRepository.deleteImagesByProductCodeIn(codes);
        productJpaRepository.deleteDetailImagesByProductCodeIn(codes);
        productJpaRepository.deleteByCodeIn(codes);
    }
}
