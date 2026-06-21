package com.kings.web.application.auth;

import com.kings.web.application.security.JwtTokenProvider;
import com.kings.web.domain.user.User;
import com.kings.web.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        validate(command);

        var user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        }

        var accessToken = jwtTokenProvider.generateAccessToken(user);

        return new LoginResult(
                accessToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationSeconds()
        );
    }

    private void validate(LoginCommand command) {
        if (!StringUtils.hasText(command.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
        }
        if (!StringUtils.hasText(command.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");
        }
    }
}
