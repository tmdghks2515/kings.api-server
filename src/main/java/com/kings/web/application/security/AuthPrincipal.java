package com.kings.web.application.security;

import com.kings.web.domain.user.Role;

import java.util.Set;

public record AuthPrincipal(
        String username,
        Set<Role> roles
) {
}
