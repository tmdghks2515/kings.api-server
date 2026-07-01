package com.kings.web.infra.presentation.user;

import com.kings.web.application.user.SignUpUserCommand;
import com.kings.web.application.user.SignUpUserService;
import com.kings.web.application.user.UserCommand;
import com.kings.web.application.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SignUpUserService signUpUserService;
    private final UserService userService;

    @PostMapping
    public void create(@RequestBody UserCommand command) {
        userService.create(command);
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpUserCommand command) {
        signUpUserService.signUp(command);
    }
}
