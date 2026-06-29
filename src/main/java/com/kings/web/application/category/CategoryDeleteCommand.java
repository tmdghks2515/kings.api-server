package com.kings.web.application.category;

import java.util.List;

public record CategoryDeleteCommand(
        List<Long> categoryIds
) {
}
