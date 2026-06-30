package com.kings.web.application.product;

import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.image.ProductDetailImage;
import com.kings.web.domain.product.image.ProductImage;
import com.kings.web.domain.product.option.ProductOption;
import com.kings.web.domain.product.option.ProductOptionType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ProductData(
        String code,
        String name,
        Double price,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        int optionCount,
        List<ProductOptionData> options,
        List<ProductImageData> images,
        List<ProductDetailImageData> detailImages
) {
    public static ProductData from(Product product) {
        return from(product, List.of());
    }

    public static ProductData from(Product product, List<FileResource> fileResources) {
        var category = product.getCategory();
        var brand = product.getBrand();
        var fileResourceByStorageKey = fileResources.stream()
                .collect(Collectors.toMap(
                        FileResource::getStorageKey,
                        Function.identity(),
                        (left, right) -> left
                ));
        var options = product.getOptions()
                .stream()
                .map(ProductOptionData::from)
                .toList();
        var images = product.getImages()
                .stream()
                .map(image -> ProductImageData.from(image, fileResourceByStorageKey.get(image.getStorageKey())))
                .toList();
        var detailImages = product.getDetailImages()
                .stream()
                .map(detailImage -> ProductDetailImageData.from(
                        detailImage,
                        fileResourceByStorageKey.get(detailImage.getStorageKey())
                ))
                .toList();

        return new ProductData(
                product.getCode(),
                product.getName(),
                product.getPrice(),
                category == null ? null : category.getId(),
                category == null ? null : category.getName(),
                brand == null ? null : brand.getId(),
                brand == null ? null : brand.getName(),
                options.size(),
                options,
                images,
                detailImages
        );
    }

    public record ProductOptionData(
            String name,
            Double price,
            ProductOptionType type
    ) {
        public static ProductOptionData from(ProductOption option) {
            return new ProductOptionData(
                    option.getName(),
                    option.getPrice(),
                    option.getType()
            );
        }
    }

    public record ProductImageData(
            Long fileResourceId,
            String originalName,
            String storageKey,
            String contentType,
            String extension,
            long sizeBytes,
            int sortOrder,
            boolean main
    ) {
        public static ProductImageData from(ProductImage image) {
            return from(image, null);
        }

        public static ProductImageData from(ProductImage image, FileResource fileResource) {
            return new ProductImageData(
                    fileResource == null ? null : fileResource.getId(),
                    fileResource == null ? null : fileResource.getOriginalName(),
                    image.getStorageKey(),
                    fileResource == null ? null : fileResource.getContentType(),
                    fileResource == null ? null : fileResource.getExtension(),
                    fileResource == null ? 0 : fileResource.getSizeBytes(),
                    image.getSortOrder(),
                    image.isMain()
            );
        }
    }

    public record ProductDetailImageData(
            Long fileResourceId,
            String originalName,
            String storageKey,
            String contentType,
            String extension,
            long sizeBytes,
            int sortOrder
    ) {
        public static ProductDetailImageData from(ProductDetailImage detailImage) {
            return from(detailImage, null);
        }

        public static ProductDetailImageData from(ProductDetailImage detailImage, FileResource fileResource) {
            return new ProductDetailImageData(
                    fileResource == null ? null : fileResource.getId(),
                    fileResource == null ? null : fileResource.getOriginalName(),
                    detailImage.getStorageKey(),
                    fileResource == null ? null : fileResource.getContentType(),
                    fileResource == null ? null : fileResource.getExtension(),
                    fileResource == null ? 0 : fileResource.getSizeBytes(),
                    detailImage.getSortOrder()
            );
        }
    }
}
