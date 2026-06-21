package com.kings.web.infra.presentation.auth;

import com.kings.web.application.auth.LoginCommand;
import com.kings.web.application.auth.LoginResult;
import com.kings.web.application.auth.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginCommand command) {
        return loginService.login(command);
    }
}
