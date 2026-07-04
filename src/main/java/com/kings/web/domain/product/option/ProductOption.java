package com.kings.web.domain.product.option;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "product_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseAuditableEntity {

    @EmbeddedId
    private ProductOptionId id;

    @MapsId("productCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_code", nullable = false, updatable = false)
    private Product product;

    @Column
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductOptionType type;

    private ProductOption(Product product, String name, Double price, ProductOptionType type) {
        this.product = Objects.requireNonNull(product, "상품은 필수입니다.");
        this.id = new ProductOptionId(product.getCode(), Objects.requireNonNull(name, "옵션명은 필수입니다."));
        this.type = Objects.requireNonNull(type, "옵션 타입은 필수입니다.");
        this.price = price;
    }

    public static ProductOption create(Product product, String name, Double price, ProductOptionType type) {
        return new ProductOption(product, name, price, type);
    }

    public void update(Double price, ProductOptionType type) {
        this.price = price;
        this.type = Objects.requireNonNull(type, "옵션 타입은 필수입니다.");
    }

    public String getProductCode() {
        return id == null ? null : id.productCode();
    }

    public String getName() {
        return id == null ? null : id.name();
    }
}
