package com.kings.web.domain.curation.detail;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MainBannerDetail.class, name = "MainBannerDetail"),
        @JsonSubTypes.Type(value = NormalBannerDetail.class, name = "NormalBannerDetail"),
        @JsonSubTypes.Type(value = CategoriesDetail.class, name = "CategoriesDetail"),
        @JsonSubTypes.Type(value = TitledProductsDetail.class, name = "TitledProductsDetail"),
        @JsonSubTypes.Type(value = ImageProductsDetail.class, name = "ImageProductsDetail"),
        @JsonSubTypes.Type(value = BrandShortcutsDetail.class, name = "BrandShortcutsDetail"),
})
public interface CurationDetail {
}
