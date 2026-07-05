package com.kings.web.application.brand;

public record BrandCommand(
        String name,
        String introduce,
        int sortOrder,
        String logoStorageKey,
        String mainImageStorageKey
) {
}
