package com.kings.web.application.brand;

import com.kings.web.application.file.FileResourceData;
import com.kings.web.domain.brand.Brand;

public record BrandData(
        Long id,
        String name,
        String introduce,
        FileResourceData logo,
        FileResourceData mainImage
) {
    public static BrandData from(Brand brand) {
        var logo = brand.getLogo();
        var mainImage = brand.getMainImage();

        return new BrandData(
                brand.getId(),
                brand.getName(),
                brand.getIntroduce(),
                logo == null ? null : FileResourceData.from(logo),
                mainImage == null ? null : FileResourceData.from(mainImage)
        );
    }
}
