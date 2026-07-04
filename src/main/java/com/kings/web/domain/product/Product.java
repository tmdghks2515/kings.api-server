package com.kings.web.domain.product;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.category.Category;
import com.kings.web.domain.product.image.ProductDetailImage;
import com.kings.web.domain.product.image.ProductImage;
import com.kings.web.domain.product.option.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseAuditableEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductOption> options = new ArrayList<>();

    @OrderBy("sortOrder ASC")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    @OrderBy("sortOrder ASC")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductDetailImage> detailImages = new ArrayList<>();

    private Product(String code, String name, Double price, Category category, Brand brand) {
        this.code = Objects.requireNonNull(code, "상품 코드는 필수입니다.");
        this.name = Objects.requireNonNull(name, "상품명은 필수입니다.");
        this.price = price;
        this.category = category;
        this.brand = brand;
    }

    public static Product create(String code, String name, Double price, Category category, Brand brand) {
        return new Product(code, name, price, category, brand);
    }

    public void update(String name, Double price, Category category, Brand brand) {
        this.name = Objects.requireNonNull(name, "상품명은 필수입니다.");
        this.price = price;
        this.category = category;
        this.brand = brand;
    }

    public void addOption(ProductOption option) {
        this.options.add(Objects.requireNonNull(option, "상품 옵션은 필수입니다."));
    }

    public void addImage(ProductImage image) {
        this.images.add(Objects.requireNonNull(image, "상품 이미지는 필수입니다."));
    }

    public void addDetailImage(ProductDetailImage detailImage) {
        this.detailImages.add(Objects.requireNonNull(detailImage, "상품 상세 이미지는 필수입니다."));
    }

    public void replaceOptions(List<ProductOption> options) {
        var replacementOptions = Objects.requireNonNull(options, "상품 옵션 목록은 필수입니다.");
        var replacementOptionNames = replacementOptions.stream()
                .map(ProductOption::getName)
                .toList();

        this.options.removeIf(option -> !replacementOptionNames.contains(option.getName()));

        for (var replacementOption : replacementOptions) {
            findOptionByName(replacementOption.getName())
                    .ifPresentOrElse(
                            option -> option.update(replacementOption.getPrice(), replacementOption.getType()),
                            () -> addOption(replacementOption)
                    );
        }
    }

    private Optional<ProductOption> findOptionByName(String name) {
        return options.stream()
                .filter(option -> Objects.equals(option.getName(), name))
                .findFirst();
    }

    public void replaceImages(List<ProductImage> images) {
        var replacementImages = Objects.requireNonNull(images, "상품 이미지 목록은 필수입니다.");
        var replacementStorageKeys = replacementImages.stream()
                .map(ProductImage::getStorageKey)
                .toList();

        this.images.removeIf(image -> !replacementStorageKeys.contains(image.getStorageKey()));

        for (var replacementImage : replacementImages) {
            findImageByStorageKey(replacementImage.getStorageKey())
                    .ifPresentOrElse(
                            image -> image.update(replacementImage.getSortOrder(), replacementImage.isMain()),
                            () -> addImage(replacementImage)
                    );
        }
    }

    public void replaceDetailImages(List<ProductDetailImage> detailImages) {
        var replacementDetailImages = Objects.requireNonNull(detailImages, "상품 상세 이미지 목록은 필수입니다.");
        var replacementStorageKeys = replacementDetailImages.stream()
                .map(ProductDetailImage::getStorageKey)
                .toList();

        this.detailImages.removeIf(detailImage -> !replacementStorageKeys.contains(detailImage.getStorageKey()));

        for (var replacementDetailImage : replacementDetailImages) {
            findDetailImageByStorageKey(replacementDetailImage.getStorageKey())
                    .ifPresentOrElse(
                            detailImage -> detailImage.updateSortOrder(replacementDetailImage.getSortOrder()),
                            () -> addDetailImage(replacementDetailImage)
                    );
        }
    }

    private Optional<ProductImage> findImageByStorageKey(String storageKey) {
        return images.stream()
                .filter(image -> Objects.equals(image.getStorageKey(), storageKey))
                .findFirst();
    }

    private Optional<ProductDetailImage> findDetailImageByStorageKey(String storageKey) {
        return detailImages.stream()
                .filter(detailImage -> Objects.equals(detailImage.getStorageKey(), storageKey))
                .findFirst();
    }
}
