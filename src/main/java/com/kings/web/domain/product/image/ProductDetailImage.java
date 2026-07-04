package com.kings.web.domain.product.image;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@Table(name = "product_detail_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetailImage extends BaseAuditableEntity {

    @EmbeddedId
    private ProductDetailImageId id;

    @MapsId("productCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_code", nullable = false, updatable = false)
    private Product product;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private ProductDetailImage(Product product, String storageKey, int sortOrder) {
        this.product = Objects.requireNonNull(product, "상품은 필수입니다.");
        this.id = new ProductDetailImageId(product.getCode(), Objects.requireNonNull(storageKey, "상품 상세 이미지 저장 키는 필수입니다."));
        this.sortOrder = sortOrder;
    }

    public static ProductDetailImage create(Product product, String storageKey, int sortOrder) {
        return new ProductDetailImage(product, storageKey, sortOrder);
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStorageKey() {
        return id == null ? null : id.storageKey();
    }
}
