package com.sparta.fitnus.user.service;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.common.exception.DuplicateEmailException;
import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.common.exception.WrongAdminTokenException;
import com.sparta.fitnus.user.dto.request.UserRequest;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    @Transactional
    public ApiResponse<UserResponse> signup(@Valid UserRequest userRequest) {
        //이메일 중복검증
        validateDuplicateEmail(userRequest.getEmail());

        UserRole role = UserRole.USER;
        //adminToken 검증
        role = validateAdminToken(userRequest, role);
        User user = User.of(userRequest, role);
        User savedUser = userRepository.save(user);
        return ApiResponse.createSuccess(new UserResponse(savedUser));
    }

    public User getUser(Long userId) {
        return userRepository.findUserById(userId);
    }

    public User getUserFromEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email " + email + " not found")
        );
    }


    public UserRole validateAdminToken(UserRequest userRequest, UserRole role) {
        if (userRequest.isAdmin()) {
            if (!ADMIN_TOKEN.equals(userRequest.getAdminToken())) {
                throw new WrongAdminTokenException();
            }
            role = UserRole.ADMIN;
        }
        return role;
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException();
        }
    }
}

