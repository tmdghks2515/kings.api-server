package com.kings.web.application.brand;

import com.kings.web.application.file.FileResourceData;
import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.file.FileResource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record BrandData(
        Long id,
        String name,
        String introduce,
        FileResourceData logo,
        FileResourceData mainImage
) {
    public static BrandData from(Brand brand) {
        return from(brand, List.of());
    }

    public static BrandData from(Brand brand, List<FileResource> fileResources) {
        var fileResourceByStorageKey = fileResources.stream()
                .collect(Collectors.toMap(
                        FileResource::getStorageKey,
                        Function.identity(),
                        (left, right) -> left
                ));
        var logo = fileResourceByStorageKey.get(brand.getLogoStorageKey());
        var mainImage = fileResourceByStorageKey.get(brand.getMainImageStorageKey());

        return new BrandData(
                brand.getId(),
                brand.getName(),
                brand.getIntroduce(),
                logo == null ? null : FileResourceData.from(logo),
                mainImage == null ? null : FileResourceData.from(mainImage)
        );
    }
}
