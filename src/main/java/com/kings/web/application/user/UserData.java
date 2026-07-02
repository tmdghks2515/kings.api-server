package com.kings.web.application.user;

import com.kings.web.domain.user.Role;
import com.kings.web.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserData(
        String username,
        String nickname,
        List<RoleData> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserData from(User user) {
        var roles = user.getRoles()
                .stream()
                .sorted()
                .map(RoleData::from)
                .toList();

        return new UserData(
                user.getUsername(),
                user.getNickname(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public record RoleData(
            Role code,
            String label
    ) {
        public static RoleData from(Role role) {
            return new RoleData(role, role.getLabel());
        }
    }
}
