package com.kings.web.infra.data.jpa.product;

import com.kings.web.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, String> {
    long countByCodeIn(List<String> codes);

    List<Product> findByCodeIn(List<String> codes);

    @Query("""
            select product
            from Product product
            left join fetch product.category
            left join fetch product.brand
            where (:keyword is null or product.code = :keyword or product.name like concat('%', :keyword, '%'))
              and (:categoryId is null or product.category.id = :categoryId)
              and (:brandId is null or product.brand.id = :brandId)
            order by product.createdAt desc
            """)
    List<Product> search(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
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
