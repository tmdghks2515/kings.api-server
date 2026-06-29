package com.kings.web.domain.link;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductDetailLink.class, name = "ProductDetailLink"),
})
public interface Link {
    String getLink();
}
