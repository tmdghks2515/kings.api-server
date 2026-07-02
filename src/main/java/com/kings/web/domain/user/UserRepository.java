package com.kings.web.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean existsByUsername(String username);

    long count();

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameWithRoles(String username);

    List<User> findAll();

    long countByUsernames(List<String> usernames);

    void deleteAllByUsernames(List<String> usernames);
}
