package com.sptek.__webFramework.security.userStore.repository;

import com.sptek.__webFramework.security.userStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User entity 조회를 위한 Spring Data JPA repository.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 로그인 username으로 사용하는 email 값으로 사용자를 조회한다.
     */
    Optional<User> findByEmail(String email);
}
