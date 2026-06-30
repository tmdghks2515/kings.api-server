package com.kings.web.application.curation;

import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageType;

public record CurationPageData(
        Long id,
        CurationPageType type,
        String typeLabel,
        int curationCount
) {
    public static CurationPageData from(CurationPage curationPage) {
        return new CurationPageData(
                curationPage.getId(),
                curationPage.getType(),
                curationPage.getType().getLabel(),
                curationPage.getCurations().size()
        );
    }
}
