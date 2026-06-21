package com.kings.web.infra.data.jpa.product;

import com.kings.web.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, String> {
}
