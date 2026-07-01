package com.kings.web.domain.user;

import lombok.Getter;

@Getter
public enum Role {
    SUPER_ADMIN("슈퍼 관리자"),
    ADMIN("관리자"),
    USER("사용자"),
    DEVELOPER("개발자"),
    ;

    private final String label;

    Role(String label) {
        this.label = label;
    }
}
