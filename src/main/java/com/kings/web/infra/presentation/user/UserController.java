package com.kings.web.infra.presentation.user;

import com.kings.web.application.user.SignUpUserCommand;
import com.kings.web.application.user.SignUpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SignUpUserService signUpUserService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpUserCommand command) {
        signUpUserService.signUp(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
