package com.kings.web.application.product;

import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.brand.BrandRepository;
import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.ProductCode;
import com.kings.web.domain.product.ProductRepository;
import com.kings.web.domain.category.Category;
import com.kings.web.domain.category.CategoryRepository;
import com.kings.web.domain.product.image.ProductDetailImage;
import com.kings.web.domain.product.image.ProductImage;
import com.kings.web.domain.product.option.ProductOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final FileResourceRepository fileResourceRepository;

    @Transactional
    public void create(ProductCommand command) {
        validate(command);

        if (productRepository.existsByCode(command.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "product code already exists");
        }

        var product = Product.create(
                command.code(),
                command.name(),
                command.price(),
                findCategory(command.categoryId()),
                findBrand(command.brandId())
        );

        for (var optionCommand : normalizedOptions(command.options())) {
            product.addOption(ProductOption.create(
                    product,
                    optionCommand.name(),
                    optionCommand.price(),
                    optionCommand.type()
            ));
        }
        for (var image : toImages(product, normalizedImages(command.images()))) {
            product.addImage(image);
        }
        for (var detailImage : toDetailImages(product, normalizedFileResourceIds(command.detailImages()))) {
            product.addDetailImage(detailImage);
        }

        productRepository.save(product);
    }

    private void validate(ProductCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product is required");
        }
        if (!StringUtils.hasText(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");
        }
        if (!ProductCode.isValid(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code must contain only letters, numbers, hyphen, or underscore");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (command.price() != null && command.price() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be greater than or equal to 0");
        }

        validateImages(normalizedImages(command.images()));
        validateFileResourceIds(normalizedFileResourceIds(command.detailImages()), "detailImages");

        var optionNames = new HashSet<String>();
        for (var option : normalizedOptions(command.options())) {
            if (!StringUtils.hasText(option.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option name is required");
            }
            if (option.price() != null && option.price() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option price must be greater than or equal to 0");
            }
            if (option.type() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option type is required");
            }
            if (!optionNames.add(option.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option name must be unique");
            }
        }
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "category not found"));
    }

    private Brand findBrand(Long brandId) {
        if (brandId == null) {
            return null;
        }

        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand not found"));
    }

    private List<ProductCommand.ProductOptionCommand> normalizedOptions(
            List<ProductCommand.ProductOptionCommand> options
    ) {
        return options == null ? List.of() : options;
    }

    private List<Long> normalizedFileResourceIds(List<Long> fileResourceIds) {
        return fileResourceIds == null ? List.of() : fileResourceIds;
    }

    private void validateFileResourceIds(List<Long> fileResourceIds, String fieldName) {
        if (fileResourceIds.stream().anyMatch(fileResourceId -> fileResourceId == null || fileResourceId <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must contain valid file ids");
        }
        if (new HashSet<>(fileResourceIds).size() != fileResourceIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be unique");
        }
    }

    private List<ProductImage> toImages(
            Product product,
            List<ProductCommand.ProductImageCommand> imageCommands
    ) {
        var fileResourceById = findFileResourcesById(imageCommands.stream()
                .map(ProductCommand.ProductImageCommand::fileResourceId)
                .toList());

        return imageCommands.stream()
                .map(imageCommand -> ProductImage.create(
                        product,
                        fileResourceById.get(imageCommand.fileResourceId()).getStorageKey(),
                        imageCommands.indexOf(imageCommand) + 1,
                        imageCommand.main()
                ))
                .toList();
    }

    private List<ProductDetailImage> toDetailImages(Product product, List<Long> fileResourceIds) {
        var fileResourceById = findFileResourcesById(fileResourceIds);

        return fileResourceIds.stream()
                .map(fileResourceId -> ProductDetailImage.create(
                        product,
                        fileResourceById.get(fileResourceId).getStorageKey(),
                        fileResourceIds.indexOf(fileResourceId) + 1
                ))
                .toList();
    }

    private Map<Long, FileResource> findFileResourcesById(List<Long> fileResourceIds) {
        if (fileResourceIds.isEmpty()) {
            return Map.of();
        }

        var fileResources = fileResourceRepository.findAllByIdIn(fileResourceIds);
        if (fileResources.size() != new HashSet<>(fileResourceIds).size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file not found");
        }

        return fileResources.stream()
                .collect(Collectors.toMap(
                        FileResource::getId,
                        Function.identity()
                ));
    }

    private List<ProductCommand.ProductImageCommand> normalizedImages(
            List<ProductCommand.ProductImageCommand> images
    ) {
        return images == null ? List.of() : images;
    }

    private void validateImages(List<ProductCommand.ProductImageCommand> images) {
        if (images.stream().anyMatch(image -> image == null || image.fileResourceId() == null || image.fileResourceId() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images must contain valid file ids");
        }

        var fileResourceIds = images.stream()
                .map(ProductCommand.ProductImageCommand::fileResourceId)
                .toList();
        if (new HashSet<>(fileResourceIds).size() != fileResourceIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images must be unique");
        }

        var mainCount = images.stream()
                .filter(ProductCommand.ProductImageCommand::main)
                .count();
        if (mainCount > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "main image must be unique");
        }
    }
}
