package com.sparta.common.user.repository;

import com.sparta.common.exception.NotFoundException;
import com.sparta.common.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    default User findUserById(Long userId) {
        return findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " not found")
        );
    }
}

