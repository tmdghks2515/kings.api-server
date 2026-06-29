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

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final FileResourceRepository fileResourceRepository;

    @Transactional
    public Long create(BrandCommand command) {
        validate(command);

        if (brandRepository.existsByName(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand name already exists");
        }

        return brandRepository.save(Brand.create(
                command.name(),
                command.introduce(),
                findFileResource(command.logoResourceId(), "logo not found"),
                findFileResource(command.mainImageResourceId())
        )).getId();
    }

    @Transactional(readOnly = true)
    public List<BrandData> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(BrandData::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandData findById(Long id) {
        return BrandData.from(getById(id));
    }

    @Transactional
    public void update(Long id, BrandCommand command) {
        validate(command);

        if (brandRepository.existsByNameAndIdNot(command.name(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand name already exists");
        }

        getById(id).update(
                command.name(),
                command.introduce(),
                findFileResource(command.logoResourceId(), "logo not found"),
                findFileResource(command.mainImageResourceId())
        );
    }

    @Transactional
    public void delete(Long id) {
        brandRepository.delete(getById(id));
    }

    private Brand getById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "brand not found"));
    }

    private FileResource findFileResource(Long fileResourceId) {
        return findFileResource(fileResourceId, "main image not found");
    }

    private FileResource findFileResource(Long fileResourceId, String notFoundMessage) {
        if (fileResourceId == null) {
            return null;
        }

        return fileResourceRepository.findById(fileResourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, notFoundMessage));
    }

    private void validate(BrandCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand is required");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (command.introduce() != null && command.introduce().length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "introduce must be less than or equal to 1000 characters");
        }
        if (command.logoResourceId() != null && command.logoResourceId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "logoResourceId must be greater than 0");
        }
        if (command.mainImageResourceId() != null && command.mainImageResourceId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mainImageResourceId must be greater than 0");
        }
    }
}
