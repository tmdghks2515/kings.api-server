package com.kings.web.application.curation;

import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.CurationType;
import com.kings.web.domain.curation.page.CurationPageType;
import com.kings.web.domain.file.FileResource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record CurationData(
        Long id,
        CurationPageType curationPageType,
        CurationType type,
        String name,
        int sortOrder,
        CurationDetailData detail
) {
    public static CurationData from(Curation curation) {
        return from(curation, List.of());
    }

    public static CurationData from(Curation curation, List<FileResource> fileResources) {
        var fileResourceByStorageKey = fileResources.stream()
                .collect(Collectors.toMap(
                        FileResource::getStorageKey,
                        Function.identity(),
                        (left, right) -> left
                ));

        return new CurationData(
                curation.getId(),
                curation.getCurationPage().getType(),
                curation.getType(),
                curation.getName(),
                curation.getSortOrder(),
                CurationDetailData.from(curation.getDetail(), fileResourceByStorageKey)
        );
    }

    public static List<String> collectImageStorageKeys(List<Curation> curations) {
        return curations.stream()
                .flatMap(curation -> CurationDetailData.collectImageStorageKeys(curation.getDetail()).stream())
                .distinct()
                .toList();
    }
}
