package com.kings.web.domain.curation.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kings.web.domain.link.Link;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageProductsDetail implements CurationDetail {
    private String imageStorageKey;
    private Link link;
    private String title;
    private String subTitle;
    private List<String> productCodes;
}
