package com.kings.web.application.curation;

import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageType;

import java.util.Comparator;
import java.util.List;

public record CurationPageDetailData(
        Long id,
        CurationPageType type,
        String typeLabel,
        List<CurationData> curations
) {
    public static CurationPageDetailData from(CurationPage curationPage) {
        return new CurationPageDetailData(
                curationPage.getId(),
                curationPage.getType(),
                curationPage.getType().getLabel(),
                curationPage.getCurations()
                        .stream()
                        .sorted(Comparator.comparingInt(Curation::getSortOrder))
                        .map(CurationData::from)
                        .toList()
        );
    }
}
