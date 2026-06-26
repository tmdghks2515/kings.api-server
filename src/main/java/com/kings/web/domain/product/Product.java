package com.kings.web.domain.product;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.product.category.ProductCategory;
import com.kings.web.domain.product.option.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseAuditableEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductOption> options = new ArrayList<>();

    private Product(String code, String name, Double price) {
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.price = price;
    }

    public static Product create(String code, String name, Double price) {
        return new Product(code, name, price);
    }

    public void addOption(ProductOption option) {
        this.options.add(Objects.requireNonNull(option, "option must not be null"));
    }
}
