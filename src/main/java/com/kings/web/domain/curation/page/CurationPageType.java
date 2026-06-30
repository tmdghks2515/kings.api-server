package com.kings.web.domain.curation.page;

import lombok.Getter;

@Getter
public enum CurationPageType {
    MAIN("메인 페이지"),
    ;

    private final String label;

    CurationPageType(String label) {
        this.label = label;
    }
}
