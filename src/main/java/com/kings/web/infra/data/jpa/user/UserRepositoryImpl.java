package com.kings.web.infra.data.jpa.user;

import com.kings.web.domain.user.User;
import com.kings.web.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsById(username);
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findById(username);
    }

    @Override
    public Optional<User> findByUsernameWithRoles(String username) {
        return userJpaRepository.findByUsernameWithRoles(username);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAllWithRoles();
    }

    @Override
    public long countByUsernames(List<String> usernames) {
        return userJpaRepository.countByUsernameIn(usernames);
    }

    @Override
    public void deleteAllByUsernames(List<String> usernames) {
        userJpaRepository.deleteRolesByUsernameIn(usernames);
        userJpaRepository.deleteByUsernameIn(usernames);
    }
}
