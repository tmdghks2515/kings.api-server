package com.kings.web.domain.link;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryLink implements Link {
    private static final String BASE_PATH = "/category/%s";

    private String categoryId;

    @Override
    public String getLink() {
        return BASE_PATH.formatted(
                UriUtils.encodePathSegment(categoryId, StandardCharsets.UTF_8)
        );
    }
}
