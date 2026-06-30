package com.kings.web.domain.link;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductDetailLink.class, name = "ProductDetailLink"),
        @JsonSubTypes.Type(value = CategoryLink.class, name = "CategoryLink"),
        @JsonSubTypes.Type(value = BrandLink.class, name = "BrandLink"),
})
public interface Link {
    @JsonIgnore
    String getLink();
}
