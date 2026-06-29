package com.kings.web.domain.product.image;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.product.Product;
import jakarta.persistence.*;
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

    @MapsId("fileResourceId")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_resource_id", nullable = false, updatable = false)
    private FileResource fileResource;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private ProductDetailImage(Product product, FileResource fileResource, int sortOrder) {
        this.product = Objects.requireNonNull(product, "product must not be null");
        this.fileResource = Objects.requireNonNull(fileResource, "fileResource must not be null");
        this.id = new ProductDetailImageId(product.getCode(), fileResource.getId());
        this.sortOrder = sortOrder;
    }

    public static ProductDetailImage create(Product product, FileResource fileResource, int sortOrder) {
        return new ProductDetailImage(product, fileResource, sortOrder);
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getFileResourceId() {
        return id == null ? null : id.fileResourceId();
    }
}
