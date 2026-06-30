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
@Table(name = "product_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseAuditableEntity {

    @EmbeddedId
    private ProductImageId id;

    @MapsId("productCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_code", nullable = false, updatable = false)
    private Product product;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean main;

    private ProductImage(Product product, String storageKey, int sortOrder, boolean main) {
        this.product = Objects.requireNonNull(product, "product must not be null");
        this.id = new ProductImageId(product.getCode(), Objects.requireNonNull(storageKey, "storageKey must not be null"));
        this.sortOrder = sortOrder;
        this.main = main;
    }

    public static ProductImage create(Product product, String storageKey, int sortOrder, boolean main) {
        return new ProductImage(product, storageKey, sortOrder, main);
    }

    public void update(int sortOrder, boolean main) {
        this.sortOrder = sortOrder;
        this.main = main;
    }

    public String getStorageKey() {
        return id == null ? null : id.storageKey();
    }
}
