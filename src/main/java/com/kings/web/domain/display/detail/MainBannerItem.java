package com.kings.web.domain.display.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kings.web.domain.link.Link;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MainBannerItem(
        String imageUrl,
        Link link,
        String title,
        String description
) {
}