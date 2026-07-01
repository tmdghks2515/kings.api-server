package com.kings.web.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                command.password()
        ));
    }
}
