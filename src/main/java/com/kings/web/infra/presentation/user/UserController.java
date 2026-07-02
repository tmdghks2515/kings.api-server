package com.kings.web.infra.presentation.user;

import com.kings.web.application.user.SignUpUserCommand;
import com.kings.web.application.user.SignUpUserService;
import com.kings.web.application.user.UserCommand;
import com.kings.web.application.user.UserData;
import com.kings.web.application.user.UserDeleteCommand;
import com.kings.web.application.user.UserService;
import com.kings.web.application.user.UserUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping
    public List<UserData> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{username}")
    public UserData findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PutMapping("/{username}")
    public void update(@PathVariable String username, @RequestBody UserUpdateCommand command) {
        userService.update(username, command);
    }

    @PostMapping("/bulk-delete")
    public void deleteAll(@RequestBody UserDeleteCommand command) {
        userService.deleteAll(command);
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpUserCommand command) {
        signUpUserService.signUp(command);
    }
}
