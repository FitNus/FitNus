package com.sparta.modulecommon.user.service;

import com.sparta.modulecommon.common.exception.NotFoundException;
import com.sparta.modulecommon.common.service.S3Service;
import com.sparta.modulecommon.user.dto.request.ProfileUpdateRequest;
import com.sparta.modulecommon.user.dto.response.ProfileAttachFileResponse;
import com.sparta.modulecommon.user.dto.response.ProfileResponse;
import com.sparta.modulecommon.user.dto.response.ProfileUpdateResponse;
import com.sparta.modulecommon.user.entity.AuthUser;
import com.sparta.modulecommon.user.entity.User;
import com.sparta.modulecommon.user.enums.UserRole;
import com.sparta.modulecommon.user.exception.UserBannedException;
import com.sparta.modulecommon.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void 파일_업로드_유저_차단() {
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
        ProfileResponse response = profileService.getUserProfile(userId);

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
        assertThrows(NotFoundException.class, () -> profileService.getUserProfile(userId));
    }

    @Test
    public void 프로필_업데이트_바이오와_닉네임_동시_업데이트() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        ProfileUpdateRequest request = new ProfileUpdateRequest("newBio", "newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser, request);

        // Then
        assertEquals("newBio", response.getBio());
        assertEquals("newNickname", response.getNickname());
        assertEquals("newBio", user.getBio());
        assertEquals("newNickname", user.getNickname());
        verify(userRepository).save(user);
    }

    @Test
    public void 프로필_바이오만_업데이트_성공() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        ProfileUpdateRequest request = new ProfileUpdateRequest("newBio", null);

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser, request);

        // Then
        assertEquals("newBio", response.getBio());
        assertEquals("oldNickname", response.getNickname());
        assertEquals("newBio", user.getBio());
        verify(userRepository).save(user);
    }

    @Test
    public void 프로필_닉네임만_업데이트_성공() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        ProfileUpdateRequest request = new ProfileUpdateRequest(null, "newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser, request);

        // Then
        assertEquals("newNickname", response.getNickname());
        verify(userRepository).save(user);
    }

    @Test
    public void 프로필_업데이트_유저를_찾을_수_없음() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> profileService.updateProfile(authUser,
                new ProfileUpdateRequest("newBio", "newNickname")));
    }

    @Test
    public void 프로필_업데이트_유저가_차단됨() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        user.deactivate();
        ProfileUpdateRequest request = new ProfileUpdateRequest("newBio", "newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UserBannedException.class,
                () -> profileService.updateProfile(authUser, request));
    }

    @Test
    void 프로필_업데이트_닉네임_유지_바이오_업데이트() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        user.updateBio("oldBio");

        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(null, "newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser,
                profileUpdateRequest);

        // Then
        assertEquals("oldBio", response.getBio()); // 바이오는 유지
        assertEquals("newNickname", response.getNickname()); // 닉네임은 업데이트됨
    }

    @Test
    void 프로필_업데이트_닉네임_업데이트_바이오_유지() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        user.updateBio("oldBio");

        // 닉네임을 업데이트하기 위한 요청
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(null, "newNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser,
                profileUpdateRequest);

        // Then
        assertEquals("oldBio", response.getBio()); // 응답에서 바이오가 기존 값을 유지하는지 확인
        assertEquals("newNickname", response.getNickname()); // 응답에서 닉네임 업데이트 확인
    }

    @Test
    void 프로필_업데이트_닉네임_제거_바이오_유지() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        user.updateBio("oldBio");

        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("newBio", null);

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser,
                profileUpdateRequest);

        // Then
        assertEquals("newBio", response.getBio()); // 바이오가 업데이트됨
        assertEquals("oldNickname", response.getNickname()); // 닉네임은 유지
    }

    @Test
    void 프로필_업데이트_닉네임_유지_바이오_제거() {
        // Given
        AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "oldNickname");
        User user = User.of("test@test.com", "password", "oldNickname", UserRole.USER);
        user.updateNickname("oldNickname");

        // 바이오를 비우기 위한 요청
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("", "oldNickname");

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        // When
        ProfileUpdateResponse response = profileService.updateProfile(authUser,
                profileUpdateRequest);

        // Then
        assertNull(response.getBio()); // 응답에서 바이오가 비워졌는지 확인
        assertEquals("oldNickname", response.getNickname()); // 응답에서 닉네임이 기존 값으로 유지되는지 확인
    }
}
