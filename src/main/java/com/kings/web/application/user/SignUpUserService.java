package com.kings.web.application.user;

import com.kings.web.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SignUpUserService {

    private final UserService userService;

    public void signUp(SignUpUserCommand command) {
        if (command == null) {
            userService.create(null);
            return;
        }

        userService.create(new UserCommand(
                command.username(),
                command.nickname(),
                command.password(),
                Set.of(Role.USER)
        ));
    }
}
