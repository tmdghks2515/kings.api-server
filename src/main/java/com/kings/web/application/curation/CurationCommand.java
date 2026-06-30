package com.kings.web.application.curation;

import com.kings.web.domain.curation.CurationType;
import com.kings.web.domain.curation.detail.CurationDetail;
import com.kings.web.domain.curation.page.CurationPageType;

public record CurationCommand(
        CurationPageType curationPageType,
        CurationType type,
        String name,
        int sortOrder,
        CurationDetail detail
) {
}
