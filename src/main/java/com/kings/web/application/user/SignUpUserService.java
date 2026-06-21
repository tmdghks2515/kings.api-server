package com.kings.web.application.user;

import com.kings.web.domain.user.Role;
import com.kings.web.domain.user.User;
import com.kings.web.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SignUpUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpUserCommand command) {
        validate(command);

        if (userRepository.existsByUsername(command.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username already exists");
        }

        var user = User.create(
                command.username(),
                command.nickname(),
                passwordEncoder.encode(command.password()),
                Set.of(Role.USER)
        );

        userRepository.save(user);
    }

    private void validate(SignUpUserCommand command) {
        if (!StringUtils.hasText(command.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
        }
        if (!StringUtils.hasText(command.nickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nickname is required");
        }
        if (!StringUtils.hasText(command.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");
        }
    }
}
