package com.kings.web.domain.product;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.product.category.ProductCategory;
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
    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductOption> options = new ArrayList<>();

    @OrderBy("sortOrder ASC")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    @OrderBy("sortOrder ASC")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductDetailImage> detailImages = new ArrayList<>();

    private Product(String code, String name, Double price, ProductCategory category) {
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.price = price;
        this.category = category;
    }

    public static Product create(String code, String name, Double price, ProductCategory category) {
        return new Product(code, name, price, category);
    }

    public void update(String name, Double price, ProductCategory category) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.price = price;
        this.category = category;
    }

    public void addOption(ProductOption option) {
        this.options.add(Objects.requireNonNull(option, "option must not be null"));
    }

    public void addImage(ProductImage image) {
        this.images.add(Objects.requireNonNull(image, "image must not be null"));
    }

    public void addDetailImage(ProductDetailImage detailImage) {
        this.detailImages.add(Objects.requireNonNull(detailImage, "detailImage must not be null"));
    }

    public void replaceOptions(List<ProductOption> options) {
        var replacementOptions = Objects.requireNonNull(options, "options must not be null");
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
        var replacementImages = Objects.requireNonNull(images, "images must not be null");
        var replacementFileResourceIds = replacementImages.stream()
                .map(ProductImage::getFileResourceId)
                .toList();

        this.images.removeIf(image -> !replacementFileResourceIds.contains(image.getFileResourceId()));

        for (var replacementImage : replacementImages) {
            findImageByFileResourceId(replacementImage.getFileResourceId())
                    .ifPresentOrElse(
                            image -> image.update(replacementImage.getSortOrder(), replacementImage.isMain()),
                            () -> addImage(replacementImage)
                    );
        }
    }

    public void replaceDetailImages(List<ProductDetailImage> detailImages) {
        var replacementDetailImages = Objects.requireNonNull(detailImages, "detailImages must not be null");
        var replacementFileResourceIds = replacementDetailImages.stream()
                .map(ProductDetailImage::getFileResourceId)
                .toList();

        this.detailImages.removeIf(detailImage -> !replacementFileResourceIds.contains(detailImage.getFileResourceId()));

        for (var replacementDetailImage : replacementDetailImages) {
            findDetailImageByFileResourceId(replacementDetailImage.getFileResourceId())
                    .ifPresentOrElse(
                            detailImage -> detailImage.updateSortOrder(replacementDetailImage.getSortOrder()),
                            () -> addDetailImage(replacementDetailImage)
                    );
        }
    }

    private Optional<ProductImage> findImageByFileResourceId(Long fileResourceId) {
        return images.stream()
                .filter(image -> Objects.equals(image.getFileResourceId(), fileResourceId))
                .findFirst();
    }

    private Optional<ProductDetailImage> findDetailImageByFileResourceId(Long fileResourceId) {
        return detailImages.stream()
                .filter(detailImage -> Objects.equals(detailImage.getFileResourceId(), fileResourceId))
                .findFirst();
    }
}
