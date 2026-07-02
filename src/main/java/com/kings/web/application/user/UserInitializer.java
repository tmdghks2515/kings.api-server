package com.kings.web.application.user;

import com.kings.web.domain.user.Role;
import com.kings.web.domain.user.User;
import com.kings.web.domain.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserInitializer {

    private static final String INITIAL_USERNAME = "admin";
    private static final String INITIAL_PASSWORD = "wndbs0805!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initialize() {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(User.create(
                INITIAL_USERNAME,
                "Super Admin",
                passwordEncoder.encode(INITIAL_PASSWORD),
                Set.of(Role.SUPER_ADMIN)
        ));
    }
}
