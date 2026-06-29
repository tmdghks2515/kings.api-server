package com.kings.web.domain.display.detail;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DisplayItemMainBannerDetail.class, name = "MainBannerDetail"),
})
public interface DisplayItemDetail {
}
