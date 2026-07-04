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
        this.username = Objects.requireNonNull(username, "아이디는 필수입니다.");
        this.nickname = Objects.requireNonNull(nickname, "닉네임은 필수입니다.");
        this.password = Objects.requireNonNull(password, "비밀번호는 필수입니다.");
        this.roles = new HashSet<>(Objects.requireNonNull(roles, "권한 목록은 필수입니다."));
    }

    public static User create(String username, String nickname, String password, Set<Role> roles) {
        return new User(username, nickname, password, roles);
    }

    public void update(String nickname, String password, Set<Role> roles) {
        this.nickname = Objects.requireNonNull(nickname, "닉네임은 필수입니다.");
        if (password != null) {
            this.password = password;
        }
        this.roles.clear();
        this.roles.addAll(Objects.requireNonNull(roles, "권한 목록은 필수입니다."));
    }
}
