package com.kings.web.application.display;

import com.kings.web.domain.display.DisplayItem;
import com.kings.web.domain.display.DisplayItemType;

public record DisplayItemData(
        Long id,
        String name,
        DisplayItemType type,
        int sortOrder
) {
    public static DisplayItemData from(DisplayItem displayItem) {
        return new DisplayItemData(
                displayItem.getId(),
                displayItem.getName(),
                displayItem.getType(),
                displayItem.getSortOrder()
        );
    }
}
