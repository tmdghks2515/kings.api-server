package com.kings.web.application.brand;

public record BrandCommand(
        String name,
        String introduce,
        Long logoResourceId,
        Long mainImageResourceId
) {
}
