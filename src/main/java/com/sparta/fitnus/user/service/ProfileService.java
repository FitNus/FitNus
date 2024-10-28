package com.sparta.fitnus.user.service;

import com.sparta.fitnus.common.exception.ProfileException;
import com.sparta.fitnus.common.service.S3Service;
import com.sparta.fitnus.user.dto.request.ProfileBioRequest;
import com.sparta.fitnus.user.dto.request.ProfileNicknameRequest;
import com.sparta.fitnus.user.dto.response.ProfileAttachFileResponse;
import com.sparta.fitnus.user.dto.response.ProfileBioResponse;
import com.sparta.fitnus.user.dto.response.ProfileNicknameResponse;
import com.sparta.fitnus.user.dto.response.ProfileResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserStatus;
import com.sparta.fitnus.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public ProfileAttachFileResponse attachFile(AuthUser authUser, MultipartFile file) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new ProfileException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new ProfileException("기능을 사용할 수 없습니다.");
        }

        if (file != null && !file.isEmpty()) {
            try {
                // 기존 파일 삭제
                String existingFileName = user.getFile(); // 기존 파일 이름 가져오기
                if (existingFileName != null) {
                    s3Service.deleteFile(existingFileName); // s3에서 기존 파일 삭제
                }
                // 새로운 파일 업로드
                String fileName = s3Service.uploadFile(file); // 파일 업로드 후 URL 반환
                user.addFile(fileName);
            } catch (IOException e) {
                throw new ProfileException("업로드 중 오류가 발생했습니다.");
            }
        }

        userRepository.save(user);
        return new ProfileAttachFileResponse(user);
    }

    public ProfileResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ProfileException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new ProfileException("기능을 사용할 수 없습니다.");
        }

        return new ProfileResponse(user.getNickname(), user.getBio(), user.getImageUrl());
    }

    @Transactional
    public ProfileBioResponse updateBio(AuthUser authUser, ProfileBioRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new ProfileException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new ProfileException("기능을 사용할 수 없습니다.");
        }

        user.updateBio(request.getBio());

        return new ProfileBioResponse(user);
    }

    @Transactional
    public ProfileNicknameResponse updateNickname(AuthUser authUser,
            ProfileNicknameRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new ProfileException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new ProfileException("기능을 사용할 수 없습니다.");
        }

        user.updateNickname(request.getNickname());

        return new ProfileNicknameResponse(user);
    }
}
