package com.kings.web.application.product;

import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.image.ProductDetailImage;
import com.kings.web.domain.product.image.ProductImage;
import com.kings.web.domain.product.option.ProductOption;
import com.kings.web.domain.product.option.ProductOptionType;

import java.util.List;

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
        var category = product.getCategory();
        var brand = product.getBrand();
        var options = product.getOptions()
                .stream()
                .map(ProductOptionData::from)
                .toList();
        var images = product.getImages()
                .stream()
                .map(ProductImageData::from)
                .toList();
        var detailImages = product.getDetailImages()
                .stream()
                .map(ProductDetailImageData::from)
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
            var fileResource = image.getFileResource();
            return new ProductImageData(
                    fileResource.getId(),
                    fileResource.getOriginalName(),
                    fileResource.getStorageKey(),
                    fileResource.getContentType(),
                    fileResource.getExtension(),
                    fileResource.getSizeBytes(),
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
            var fileResource = detailImage.getFileResource();
            return new ProductDetailImageData(
                    fileResource.getId(),
                    fileResource.getOriginalName(),
                    fileResource.getStorageKey(),
                    fileResource.getContentType(),
                    fileResource.getExtension(),
                    fileResource.getSizeBytes(),
                    detailImage.getSortOrder()
            );
        }
    }
}
