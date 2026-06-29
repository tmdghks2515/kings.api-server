package com.kings.web.domain.curation.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kings.web.domain.link.ImageLink;
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
public class CategoriesDetail implements CurationDetail {
    private List<ImageLink> items;
}
