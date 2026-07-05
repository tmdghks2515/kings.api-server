package com.kings.web.application.brand;

import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.brand.BrandRepository;
import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final FileResourceRepository fileResourceRepository;

    @Transactional
    public Long create(BrandCommand command) {
        validate(command);

        if (brandRepository.existsByName(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 브랜드명입니다.");
        }

        return brandRepository.save(Brand.create(
                command.name(),
                command.introduce(),
                command.sortOrder(),
                normalizeStorageKey(command.logoStorageKey()),
                normalizeStorageKey(command.mainImageStorageKey())
        )).getId();
    }

    @Transactional(readOnly = true)
    public List<BrandData> findAll() {
        var brands = brandRepository.findAllOrderBySortOrder();
        var fileResources = findBrandFileResources(brands);

        return brands.stream()
                .map(brand -> BrandData.from(brand, fileResources))
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandData findById(Long id) {
        var brand = getById(id);
        return BrandData.from(brand, findBrandFileResources(List.of(brand)));
    }

    @Transactional
    public void update(Long id, BrandCommand command) {
        validate(command);

        if (brandRepository.existsByNameAndIdNot(command.name(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 브랜드명입니다.");
        }

        getById(id).update(
                command.name(),
                command.introduce(),
                command.sortOrder(),
                normalizeStorageKey(command.logoStorageKey()),
                normalizeStorageKey(command.mainImageStorageKey())
        );
    }

    @Transactional
    public void delete(Long id) {
        brandRepository.delete(getById(id));
    }

    private Brand getById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "브랜드를 찾을 수 없습니다."));
    }

    private List<FileResource> findBrandFileResources(List<Brand> brands) {
        var storageKeys = new LinkedHashSet<String>();

        for (var brand : brands) {
            if (StringUtils.hasText(brand.getLogoStorageKey())) {
                storageKeys.add(brand.getLogoStorageKey());
            }
            if (StringUtils.hasText(brand.getMainImageStorageKey())) {
                storageKeys.add(brand.getMainImageStorageKey());
            }
        }

        if (storageKeys.isEmpty()) {
            return List.of();
        }

        return fileResourceRepository.findAllByStorageKeyIn(List.copyOf(storageKeys));
    }

    private void validate(BrandCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "브랜드 정보를 입력해 주세요.");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "브랜드명을 입력해 주세요.");
        }
        if (command.introduce() != null && command.introduce().length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "브랜드 소개는 1000자 이하로 입력해 주세요.");
        }
        if (command.sortOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서는 0 이상이어야 합니다.");
        }
        validateStorageKey(command.logoStorageKey(), "로고 이미지를 찾을 수 없습니다.");
        validateStorageKey(command.mainImageStorageKey(), "메인 이미지를 찾을 수 없습니다.");
    }

    private void validateStorageKey(String storageKey, String notFoundMessage) {
        var normalizedStorageKey = normalizeStorageKey(storageKey);
        if (normalizedStorageKey == null) {
            return;
        }

        if (!fileResourceRepository.existsByStorageKey(normalizedStorageKey)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, notFoundMessage);
        }
    }

    private String normalizeStorageKey(String storageKey) {
        return StringUtils.hasText(storageKey) ? storageKey.trim() : null;
    }
}
