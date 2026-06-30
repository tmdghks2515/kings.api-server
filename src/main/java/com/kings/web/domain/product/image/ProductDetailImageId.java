package com.kings.web.domain.product.image;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record ProductDetailImageId(
        @Column(name = "product_code", nullable = false, updatable = false, length = 50)
        String productCode,

        @Column(name = "storage_key", nullable = false, updatable = false, length = 500)
        String storageKey
) implements Serializable {
}
