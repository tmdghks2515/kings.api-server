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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(UserCommand command) {
        validate(command);

        if (userRepository.existsByUsername(command.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        var user = User.create(
                command.username(),
                command.nickname(),
                passwordEncoder.encode(command.password()),
                resolveRoles(command.roles())
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserData> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserData::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserData findByUsername(String username) {
        return UserData.from(getByUsername(username));
    }

    @Transactional
    public void update(String username, UserUpdateCommand command) {
        validateUpdateCommand(command);

        var user = getByUsername(username);
        user.update(
                command.nickname(),
                StringUtils.hasText(command.password()) ? passwordEncoder.encode(command.password()) : null,
                resolveRoles(command.roles())
        );
    }

    @Transactional
    public void deleteAll(UserDeleteCommand command) {
        var usernames = validateDeleteCommand(command);

        if (userRepository.countByUsernames(usernames) != usernames.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        userRepository.deleteAllByUsernames(usernames);
    }

    private User getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디를 입력해 주세요.");
        }

        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private void validate(UserCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 정보를 입력해 주세요.");
        }
        if (!StringUtils.hasText(command.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디를 입력해 주세요.");
        }
        if (!StringUtils.hasText(command.nickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임을 입력해 주세요.");
        }
        if (!StringUtils.hasText(command.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호를 입력해 주세요.");
        }
        if (command.roles() != null && command.roles().stream().anyMatch(role -> role == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 목록에 빈 값이 포함될 수 없습니다.");
        }
    }

    private void validateUpdateCommand(UserUpdateCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 정보를 입력해 주세요.");
        }
        if (!StringUtils.hasText(command.nickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임을 입력해 주세요.");
        }
        if (command.roles() != null && command.roles().stream().anyMatch(role -> role == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 목록에 빈 값이 포함될 수 없습니다.");
        }
    }

    private Set<Role> resolveRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(Role.USER);
        }

        return Set.copyOf(roles);
    }

    private List<String> validateDeleteCommand(UserDeleteCommand command) {
        if (command == null || command.usernames() == null || command.usernames().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 사용자를 선택해 주세요.");
        }

        var usernames = command.usernames();
        if (usernames.stream().anyMatch(username -> !StringUtils.hasText(username))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 아이디에 빈 값이 포함될 수 없습니다.");
        }
        if (new HashSet<>(usernames).size() != usernames.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 사용자 목록에 중복된 아이디가 있습니다.");
        }

        return usernames;
    }
}
