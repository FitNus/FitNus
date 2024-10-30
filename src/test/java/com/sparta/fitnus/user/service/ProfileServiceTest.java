package com.sparta.fitnus.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.common.service.S3Service;
import com.sparta.fitnus.user.dto.request.ProfileBioRequest;
import com.sparta.fitnus.user.dto.request.ProfileNicknameRequest;
import com.sparta.fitnus.user.dto.response.ProfileAttachFileResponse;
import com.sparta.fitnus.user.dto.response.ProfileBioResponse;
import com.sparta.fitnus.user.dto.response.ProfileNicknameResponse;
import com.sparta.fitnus.user.dto.response.ProfileResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.exception.UserBannedException;
import com.sparta.fitnus.user.repository.UserRepository;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile multipartFile;

    @Test
    public void 파일_업로드_성공() throws IOException {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "test", UserRole.USER);
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(s3Service.uploadFile(multipartFile)).thenReturn("uploadedFileUrl");

        // When
        ProfileAttachFileResponse response = profileService.attachFile(authUser, multipartFile);

        // Then
        assertEquals("uploadedFileUrl", response.getImageUrl());
        verify(s3Service).uploadFile(multipartFile);
        verify(userRepository).save(user);
    }

    @Test
    public void 파일_업로드_유저_찾을_수_없음() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> profileService.attachFile(authUser, multipartFile));
    }

    @Test
    public void 파일_업로드_유저_차단됨() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "test", UserRole.USER);
        user.deactivate();

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UserBannedException.class,
                () -> profileService.attachFile(authUser, multipartFile));
    }

    @Test
    public void 파일_삭제_성공() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "test", UserRole.USER);
        user.addFile("existingFileName");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        profileService.deleteFile(authUser);

        // Then
        verify(s3Service).deleteFile("existingFileName");
        verify(userRepository).save(user);
    }

    @Test
    public void 사용자_조회_성공() {
        // Given
        Long userId = 1L;
        User user = User.of("test@test.com", "password", "test", UserRole.USER);
        user.addFile("imageUrl");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        ProfileResponse response = profileService.getUser(userId);

        // Then
        assertEquals("test", response.getNickname());
        assertNull(response.getBio());
        assertEquals("imageUrl", response.getImageUrl());
    }

    @Test
    public void 사용자_조회_유저_찾을_수_없음() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> profileService.getUser(userId));
    }

    @Test
    public void 바이오_업데이트_성공() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "test", UserRole.USER);
        ProfileBioRequest request = new ProfileBioRequest("test bio");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileBioResponse response = profileService.updateBio(authUser, request);

        // Then
        assertEquals("test bio", response.getBio());
        assertEquals("test bio", user.getBio());
        verify(userRepository).save(user);
    }

    @Test
    public void 바이오_업데이트_유저가_차단됨() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "test", UserRole.USER);
        user.deactivate();

        ProfileBioRequest request = new ProfileBioRequest("test Bio");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UserBannedException.class, () -> {
            profileService.updateBio(authUser, request);
        });
    }

    @Test
    public void 닉네임_업데이트_성공() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        ProfileNicknameRequest request = new ProfileNicknameRequest("newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileNicknameResponse response = profileService.updateNickname(authUser, request);

        // Then
        assertEquals("newNickname", response.getNickname());
        assertEquals("newNickname", user.getNickname());
        verify(userRepository).save(user);
    }

    @Test
    public void 닉네임_업데이트_유저가_차단됨() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        user.deactivate();
        ProfileNicknameRequest request = new ProfileNicknameRequest("newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UserBannedException.class, () -> {
            profileService.updateNickname(authUser, request);
        });
    }
}
