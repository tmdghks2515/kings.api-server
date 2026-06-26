package com.kings.web.application.display;

import com.kings.web.domain.display.DisplayItemType;

public record DisplayItemCommand(
        String name,
        DisplayItemType type,
        int sortOrder
) {
}
