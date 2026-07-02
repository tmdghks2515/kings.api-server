package com.kings.web.infra.data.jpa.brand;

import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.brand.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final BrandJpaRepository brandJpaRepository;

    @Override
    public Brand save(Brand brand) {
        return brandJpaRepository.save(brand);
    }

    @Override
    public List<Brand> findAll() {
        return brandJpaRepository.findAll();
    }

    @Override
    public List<Brand> findAllOrderBySortOrder() {
        return brandJpaRepository.findAllByOrderBySortOrderAscIdAsc();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandJpaRepository.findById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return brandJpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return brandJpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public void delete(Brand brand) {
        brandJpaRepository.delete(brand);
    }
}
