package com.kings.web.application.user;

import com.kings.web.domain.user.Role;

import java.util.Set;

public record UserUpdateCommand(
        String nickname,
        String password,
        Set<Role> roles
) {
}
