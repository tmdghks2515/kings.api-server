package com.kings.web.infra.presentation;

public record ApiErrorResponse(
        int status,
        String message
) {
}
