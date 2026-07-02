package com.kings.web.infra.data.jpa.user;

import com.kings.web.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, String> {
    @Query("select distinct u from User u left join fetch u.roles order by u.createdAt desc")
    List<User> findAllWithRoles();

    @Query("select distinct u from User u left join fetch u.roles where u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    long countByUsernameIn(List<String> usernames);

    @Modifying
    @Query(value = "delete from user_role where username in (:usernames)", nativeQuery = true)
    void deleteRolesByUsernameIn(@Param("usernames") List<String> usernames);

    @Modifying
    @Query("delete from User u where u.username in :usernames")
    void deleteByUsernameIn(@Param("usernames") List<String> usernames);
}
