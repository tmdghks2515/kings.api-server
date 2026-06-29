package com.kings.web.application.display;

import com.kings.web.domain.display.DisplayItemType;

public record DisplayItemCommand(
        DisplayItemType type,
        int sortOrder
) {
}
