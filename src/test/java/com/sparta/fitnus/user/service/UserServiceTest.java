package com.sparta.fitnus.user.service;

import com.sparta.fitnus.user.dto.request.ChangePasswordRequest;
import com.sparta.fitnus.user.dto.request.UserRequest;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.enums.UserStatus;
import com.sparta.fitnus.user.exception.DuplicateEmailException;
import com.sparta.fitnus.user.exception.WrongPasswordException;
import com.sparta.fitnus.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisUserService redisUserService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        // ADMIN_TOKEN과 OWNER_TOKEN 값 설정
        ReflectionTestUtils.setField(userService, "ADMIN_TOKEN", "adminToken");
        ReflectionTestUtils.setField(userService, "OWNER_TOKEN", "ownerToken");

        // 테스트용 User 및 UserRequest 객체 생성
        user = User.of("test@example.com", "encodedPassword", "testUser", UserRole.USER);
        userRequest = UserRequest.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .admin(false)
                .owner(false)
                .ownerToken("")
                .adminToken("")
                .build();
    }

    @Nested
    class SignupTests {

        @Test
        void signup_성공() {
            // given
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            // when
            UserResponse response = userService.signup(userRequest);

            // then
            assertNotNull(response);
            assertEquals(user.getId(), response.getId());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void signup_실패_ThrowDuplicateEmailException_whenEmailExists() {
            // given
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));

            // when & then
            assertThrows(DuplicateEmailException.class, () -> userService.signup(userRequest));
        }
    }

    @Nested
    class ChangePasswordTests {

        @Test
        void changePassword_성공_whenValidRequest() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@example.com", "testUser");
            ChangePasswordRequest changeRequest = ChangePasswordRequest.builder()
                    .oldPassword("password")
                    .newPassword("newPassword")
                    .build();

            // userRepository에서 user를 반환하도록 설정
            when(userRepository.findUserById(1L)).thenReturn(user);
            when(passwordEncoder.matches(changeRequest.getOldPassword(), user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(changeRequest.getNewPassword())).thenReturn("encodedNewPassword");

            // when
            String result = userService.changePassword(authUser, 1L, changeRequest);

            // then
            assertEquals("비밀번호 변경 완료", result);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void changePassword_실패_ThrowWrongPasswordException_whenOldPasswordIsIncorrect() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@example.com", "testUser");
            ChangePasswordRequest changeRequest = ChangePasswordRequest.builder()
                    .oldPassword("wrongPassword")
                    .newPassword("newPassword")
                    .build();

            // userRepository에서 user를 반환하도록 설정
            when(userRepository.findUserById(1L)).thenReturn(user);
            when(passwordEncoder.matches(changeRequest.getOldPassword(), user.getPassword())).thenReturn(false);

            // when & then
            assertThrows(WrongPasswordException.class, () -> userService.changePassword(authUser, 1L, changeRequest));
        }
    }


    @Nested
    class DeleteUserTests {

        @Test
        void deleteUser_성공_whenValidRequest() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@example.com", "testUser");
            when(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())).thenReturn(true);
            when(userRepository.findUserById(1L)).thenReturn(user); // 필요한 테스트에만 설정

            // when
            userService.deleteUser(authUser, 1L, userRequest);

            // then
            verify(userRepository, times(1)).delete(user);
            verify(redisUserService, times(1)).deleteTokens(String.valueOf(authUser.getId()));
        }
    }

    @Nested
    class CheckLoginTests {

        @Test
        void checkLogin_성공_whenValidCredentials() {
            // given
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())).thenReturn(true);

            // when
            User result = userService.checkLogin(userRequest);

            // then
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
        }

        @Test
        void checkLogin_실패_whenPasswordIsIncorrect() {
            // given
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())).thenReturn(false);

            // when & then
            assertThrows(WrongPasswordException.class, () -> userService.checkLogin(userRequest));
        }
    }

    @Nested
    class DeactivateUserTests {

        @Test
        void deactivateUser_성공_whenValidUser() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.ADMIN, "test@example.com", "testUser");
            when(userRepository.findUserById(1L)).thenReturn(user);

            // when
            String result = userService.deactivateUser(1L, authUser);

            // then
            assertEquals("유저 비활성화 완료", result);
            assertEquals(UserStatus.BANNED, user.getStatus());
            verify(userRepository, times(1)).save(user);
            verify(redisUserService, times(1)).deleteTokens("1");
        }
    }

    @Nested
    class ValidateDuplicateEmailTests {

        @Test
        void validateDuplicateEmail_실패_whenEmailExists() {
            // given
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

            // when & then
            assertThrows(DuplicateEmailException.class, () -> userService.validateDuplicateEmail(user.getEmail()));
        }
    }
}
