package com.kings.web.domain.link;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BrandLink implements Link {
    private static final String BASE_PATH = "/brand/%s";

    private String brandId;

    @Override
    public String getLink() {
        return BASE_PATH.formatted(
                UriUtils.encodePathSegment(brandId, StandardCharsets.UTF_8)
        );
    }
}
