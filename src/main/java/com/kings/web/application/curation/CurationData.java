package com.kings.web.application.curation;

import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.CurationType;
import com.kings.web.domain.curation.detail.CurationDetail;

public record CurationData(
        Long id,
        CurationType type,
        String name,
        int sortOrder,
        CurationDetail detail
) {
    public static CurationData from(Curation curation) {
        return new CurationData(
                curation.getId(),
                curation.getType(),
                curation.getName(),
                curation.getSortOrder(),
                curation.getDetail()
        );
    }
}
