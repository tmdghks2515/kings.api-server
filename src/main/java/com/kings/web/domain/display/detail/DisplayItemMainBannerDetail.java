package com.kings.web.domain.display.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class DisplayItemMainBannerDetail implements DisplayItemDetail {
    private List<DisplayItemMainBannerItem> items;
}
