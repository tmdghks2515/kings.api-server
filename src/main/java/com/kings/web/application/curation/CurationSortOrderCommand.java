package com.kings.web.application.curation;

public record CurationSortOrderCommand(
        Long id,
        int sortOrder
) {
}
