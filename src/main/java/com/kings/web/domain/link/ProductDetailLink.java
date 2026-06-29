package com.kings.web.domain.link;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailLink implements Link {

    private static final String BASE_PATH = "/products/%s";

    private String productCode;

    @Override
    public String getLink() {
        return BASE_PATH.formatted(
                UriUtils.encodePathSegment(productCode, StandardCharsets.UTF_8)
        );
    }
}
