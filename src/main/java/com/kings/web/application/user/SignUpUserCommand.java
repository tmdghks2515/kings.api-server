package com.kings.web.application.user;

public record SignUpUserCommand(
        String username,
        String nickname,
        String password
) {
}
