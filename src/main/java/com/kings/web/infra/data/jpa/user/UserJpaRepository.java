package com.kings.web.infra.data.jpa.user;

import com.kings.web.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, String> {
}
