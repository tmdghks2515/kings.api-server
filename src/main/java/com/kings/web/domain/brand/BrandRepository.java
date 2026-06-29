package com.kings.web.domain.brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Brand save(Brand brand);

    List<Brand> findAll();

    Optional<Brand> findById(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    void delete(Brand brand);
}
