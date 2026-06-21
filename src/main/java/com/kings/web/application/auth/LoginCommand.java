package com.kings.web.application.auth;

public record LoginCommand(
        String username,
        String password
) {
}
