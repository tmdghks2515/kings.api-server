package com.kings.web.domain.curation.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kings.web.domain.link.ImageLink;
import com.kings.web.domain.link.Link;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurationItem extends ImageLink {
    private String title;
    private String description;
}
