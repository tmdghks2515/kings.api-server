package com.kings.web.application.auth;

public record LoginResult(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
