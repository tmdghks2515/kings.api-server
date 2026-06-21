package com.kings.web.domain.product.option;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record ProductOptionId(
        @Column(name = "product_code", nullable = false, updatable = false, length = 50)
        String productCode,

        @Column(name = "name", nullable = false, updatable = false, length = 100)
        String name
) implements Serializable {
}
