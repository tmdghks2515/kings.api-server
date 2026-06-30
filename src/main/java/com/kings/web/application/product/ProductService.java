package com.kings.web.application.product;

import com.kings.web.application.file.FileStorage;
import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.brand.BrandRepository;
import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final FileResourceRepository fileResourceRepository;
    private final FileStorage fileStorage;

    @Transactional(readOnly = true)
    public List<ProductData> findAll() {
        var products = productRepository.findAll();
        var fileResources = findImageFileResources(products);

        return products.stream()
                .map(product -> ProductData.from(product, fileResources))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductData findByCode(String code) {
        var product = getByCode(code);
        return ProductData.from(product, findImageFileResources(List.of(product)));
    }

    @Transactional
    public void update(String code, ProductCommand command) {
        validateUpdateCommand(code, command);

        var product = getByCode(code);
        product.update(command.name(), command.price(), findCategory(command.categoryId()), findBrand(command.brandId()));
        product.replaceOptions(toOptions(product, normalizedOptions(command.options())));
        product.replaceImages(toImages(product, normalizedImages(command.images())));
        product.replaceDetailImages(toDetailImages(product, normalizedFileResourceIds(command.detailImages())));
    }

    @Transactional
    public void deleteAll(ProductDeleteCommand command) {
        var productIds = validateDeleteCommand(command);
        var products = productRepository.findAllByCodes(productIds);

        if (products.size() != productIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found");
        }

        var fileResources = collectImageFileResources(products);

        productRepository.deleteAllByCodes(productIds);
        fileResourceRepository.deleteAll(fileResources);

        for (var fileResource : fileResources) {
            fileStorage.delete(fileResource.getStorageKey());
        }
    }

    private Product getByCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product code is required");
        }
        if (!ProductCode.isValid(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product code must contain only letters, numbers, hyphen, or underscore");
        }

        return productRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
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

    private void validateUpdateCommand(String code, ProductCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product is required");
        }
        if (StringUtils.hasText(command.code()) && !ProductCode.isValid(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product code must contain only letters, numbers, hyphen, or underscore");
        }
        if (StringUtils.hasText(command.code()) && !Objects.equals(code, command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product code must not be changed");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (command.price() != null && command.price() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be greater than or equal to 0");
        }

        validateOptions(normalizedOptions(command.options()));
        validateImages(normalizedImages(command.images()));
        validateFileResourceIds(normalizedFileResourceIds(command.detailImages()), "detailImages");
    }

    private void validateOptions(List<ProductCommand.ProductOptionCommand> options) {
        var optionNames = new HashSet<String>();
        for (var option : options) {
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

    private List<ProductOption> toOptions(
            Product product,
            List<ProductCommand.ProductOptionCommand> optionCommands
    ) {
        return optionCommands.stream()
                .map(optionCommand -> ProductOption.create(
                        product,
                        optionCommand.name(),
                        optionCommand.price(),
                        optionCommand.type()
                ))
                .toList();
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

    private List<String> validateDeleteCommand(ProductDeleteCommand command) {
        if (command == null || command.productIds() == null || command.productIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productIds is required");
        }

        var productIds = command.productIds();
        if (productIds.stream().anyMatch(productId -> !StringUtils.hasText(productId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }
        if (new HashSet<>(productIds).size() != productIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productIds must be unique");
        }

        return productIds;
    }

    private List<FileResource> collectImageFileResources(List<Product> products) {
        return findImageFileResources(products);
    }

    private List<FileResource> findImageFileResources(List<Product> products) {
        var storageKeys = new LinkedHashSet<String>();
        for (var product : products) {
            for (var image : product.getImages()) {
                storageKeys.add(image.getStorageKey());
            }
            for (var detailImage : product.getDetailImages()) {
                storageKeys.add(detailImage.getStorageKey());
            }
        }

        if (storageKeys.isEmpty()) {
            return List.of();
        }

        return fileResourceRepository.findAllByStorageKeyIn(List.copyOf(storageKeys));
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
