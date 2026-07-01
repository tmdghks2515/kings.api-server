package com.kings.web.application.user;

public record UserCommand(
        String username,
        String nickname,
        String password
) {
}
