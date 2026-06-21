package com.kings.web.domain.user;

import com.kings.web.domain.audit.BaseAuditableEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseAuditableEntity {

    @Id
    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "username"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<Role> roles = new HashSet<>();

    private User(String username, String nickname, String password, Set<Role> roles) {
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.nickname = Objects.requireNonNull(nickname, "nickname must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.roles = new HashSet<>(Objects.requireNonNull(roles, "roles must not be null"));
    }

    public static User create(String username, String nickname, String password, Set<Role> roles) {
        return new User(username, nickname, password, roles);
    }
}
