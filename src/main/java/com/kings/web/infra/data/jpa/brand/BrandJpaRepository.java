package com.kings.web.infra.data.jpa.brand;

import com.kings.web.domain.brand.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandJpaRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByOrderBySortOrderAscIdAsc();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
