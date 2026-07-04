package com.kings.web.application.product;

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 상품 코드입니다.");
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
        for (var detailImage : toDetailImages(product, normalizedImageStorageKeys(command.detailImages()))) {
            product.addDetailImage(detailImage);
        }

        productRepository.save(product);
    }

    private void validate(ProductCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 정보를 입력해 주세요.");
        }
        if (!StringUtils.hasText(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 코드를 입력해 주세요.");
        }
        if (!ProductCode.isValid(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 코드는 영문, 숫자, 하이픈(-), 밑줄(_)만 사용할 수 있습니다.");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명을 입력해 주세요.");
        }
        if (command.price() != null && command.price() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        }

        var images = normalizedImages(command.images());
        var detailImages = normalizedImageStorageKeys(command.detailImages());
        validateImages(images);
        validateImageStorageKeys(detailImages, "detailImages");
        validateImageStorageKeysExist(collectImageStorageKeys(images, detailImages));

        var optionNames = new HashSet<String>();
        for (var option : normalizedOptions(command.options())) {
            if (!StringUtils.hasText(option.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션명을 입력해 주세요.");
            }
            if (option.price() != null && option.price() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 가격은 0 이상이어야 합니다.");
            }
            if (option.type() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 타입을 선택해 주세요.");
            }
            if (!optionNames.add(option.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션명은 중복될 수 없습니다.");
            }
        }
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."));
    }

    private Brand findBrand(Long brandId) {
        if (brandId == null) {
            return null;
        }

        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "브랜드를 찾을 수 없습니다."));
    }

    private List<ProductCommand.ProductOptionCommand> normalizedOptions(
            List<ProductCommand.ProductOptionCommand> options
    ) {
        return options == null ? List.of() : options;
    }

    private List<String> normalizedImageStorageKeys(List<String> imageStorageKeys) {
        return imageStorageKeys == null ? List.of() : imageStorageKeys;
    }

    private void validateImageStorageKeys(List<String> imageStorageKeys, String fieldName) {
        if (imageStorageKeys.stream().anyMatch(imageStorageKey -> !StringUtils.hasText(imageStorageKey))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "에는 올바른 이미지 저장 키만 입력할 수 있습니다.");
        }
        if (new HashSet<>(imageStorageKeys).size() != imageStorageKeys.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "에는 중복된 이미지 저장 키를 입력할 수 없습니다.");
        }
    }

    private List<ProductImage> toImages(
            Product product,
            List<ProductCommand.ProductImageCommand> imageCommands
    ) {
        return imageCommands.stream()
                .map(imageCommand -> ProductImage.create(
                        product,
                        imageCommand.imageStorageKey(),
                        imageCommands.indexOf(imageCommand) + 1,
                        imageCommand.main()
                ))
                .toList();
    }

    private List<ProductDetailImage> toDetailImages(Product product, List<String> imageStorageKeys) {
        return imageStorageKeys.stream()
                .map(imageStorageKey -> ProductDetailImage.create(
                        product,
                        imageStorageKey,
                        imageStorageKeys.indexOf(imageStorageKey) + 1
                ))
                .toList();
    }

    private void validateImageStorageKeysExist(List<String> imageStorageKeys) {
        if (imageStorageKeys.isEmpty()) {
            return;
        }

        var fileResources = fileResourceRepository.findAllByStorageKeyIn(imageStorageKeys);
        if (fileResources.size() != imageStorageKeys.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일을 찾을 수 없습니다.");
        }
    }

    private List<ProductCommand.ProductImageCommand> normalizedImages(
            List<ProductCommand.ProductImageCommand> images
    ) {
        return images == null ? List.of() : images;
    }

    private void validateImages(List<ProductCommand.ProductImageCommand> images) {
        if (images.stream().anyMatch(image -> image == null || !StringUtils.hasText(image.imageStorageKey()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 이미지에는 올바른 이미지 저장 키만 입력할 수 있습니다.");
        }

        var imageStorageKeys = images.stream()
                .map(ProductCommand.ProductImageCommand::imageStorageKey)
                .toList();
        if (new HashSet<>(imageStorageKeys).size() != imageStorageKeys.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 이미지에는 중복된 이미지 저장 키를 입력할 수 없습니다.");
        }

        var mainCount = images.stream()
                .filter(ProductCommand.ProductImageCommand::main)
                .count();
        if (mainCount > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대표 이미지는 하나만 선택할 수 있습니다.");
        }
    }

    private List<String> collectImageStorageKeys(
            List<ProductCommand.ProductImageCommand> images,
            List<String> detailImages
    ) {
        var imageStorageKeys = new HashSet<String>();
        for (var image : images) {
            imageStorageKeys.add(image.imageStorageKey());
        }
        imageStorageKeys.addAll(detailImages);
        return List.copyOf(imageStorageKeys);
    }
}
