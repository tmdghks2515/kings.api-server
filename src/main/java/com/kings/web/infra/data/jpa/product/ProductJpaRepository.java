package com.kings.web.infra.data.jpa.product;

import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.ProductMainImageStorageKeyData;
import com.kings.web.domain.product.ProductOptionNameData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, String> {
    long countByCodeIn(List<String> codes);

    @EntityGraph(attributePaths = "brand")
    List<Product> findByCodeIn(List<String> codes);

    @Query("""
            select new com.kings.web.domain.product.ProductMainImageStorageKeyData(
                productImage.product.code,
                productImage.id.storageKey
            )
            from ProductImage productImage
            where productImage.product.code in :codes
              and productImage.main = true
            """)
    List<ProductMainImageStorageKeyData> findMainImageStorageKeysByProductCodeIn(@Param("codes") List<String> codes);

    @Query("""
            select new com.kings.web.domain.product.ProductOptionNameData(
                productOption.product.code,
                productOption.id.name
            )
            from ProductOption productOption
            where productOption.product.code in :codes
            order by productOption.createdAt asc
            """)
    List<ProductOptionNameData> findOptionNamesByProductCodeIn(@Param("codes") List<String> codes);

    @Query("""
            select product
            from Product product
            left join fetch product.category
            left join fetch product.brand
            where (:keyword is null or product.code = :keyword or product.name like concat('%', :keyword, '%'))
              and (:categoryFilter = false or product.category.id in :categoryIds)
              and (:brandId is null or product.brand.id = :brandId)
            order by product.createdAt desc
            """)
    List<Product> search(
            @Param("keyword") String keyword,
            @Param("categoryFilter") boolean categoryFilter,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("brandId") Long brandId
    );

    @Modifying
    @Query("delete from ProductOption productOption where productOption.product.code in :codes")
    void deleteOptionsByProductCodeIn(@Param("codes") List<String> codes);

    @Modifying
    @Query("delete from ProductImage productImage where productImage.product.code in :codes")
    void deleteImagesByProductCodeIn(@Param("codes") List<String> codes);

    @Modifying
    @Query("delete from ProductDetailImage productDetailImage where productDetailImage.product.code in :codes")
    void deleteDetailImagesByProductCodeIn(@Param("codes") List<String> codes);

    @Modifying
    @Query("delete from Product product where product.code in :codes")
    void deleteByCodeIn(@Param("codes") List<String> codes);
}
