package com.kings.web.application.curation;

import com.kings.web.domain.curation.CurationType;
import com.kings.web.domain.curation.detail.CurationDetail;

public record CurationCommand(
        CurationType type,
        String name,
        int sortOrder,
        CurationDetail detail
) {
}
