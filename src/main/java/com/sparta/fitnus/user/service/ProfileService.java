package com.sparta.fitnus.user.service;

import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.common.service.S3Service;
import com.sparta.fitnus.user.dto.request.ProfileUpdateRequest;
import com.sparta.fitnus.user.dto.response.ProfileAttachFileResponse;
import com.sparta.fitnus.user.dto.response.ProfileResponse;
import com.sparta.fitnus.user.dto.response.ProfileUpdateResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserStatus;
import com.sparta.fitnus.user.exception.ProfileUploadException;
import com.sparta.fitnus.user.exception.UserBannedException;
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
                new NotFoundException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UserBannedException();
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
                throw new ProfileUploadException();
            }
        }

        userRepository.save(user);
        return new ProfileAttachFileResponse(user);
    }

    @Transactional
    public void deleteFile(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new NotFoundException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UserBannedException();
        }

        String fileName = user.getFile(); // 사용자 파일 이름 가져오기

        if (fileName != null) {
            s3Service.deleteFile(fileName); // s3에서 파일. 삭제
            user.removeFile(); // 파일 정보 제거
            userRepository.save(user); // 변경사항 저장
        }
    }

    public ProfileResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UserBannedException();
        }

        return new ProfileResponse(user);
    }

    @Transactional
    public ProfileUpdateResponse updateProfile(AuthUser authUser, ProfileUpdateRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new NotFoundException("유저를 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UserBannedException();
        }

        // Bio 업데이트
        if (request.getBio() != null) {
            user.updateBio(request.getBio());
        }

        // Nickname 업데이트
        if (request.getNickname() != null) {
            user.updateNickname(request.getNickname());
        }

        userRepository.save(user);
        return new ProfileUpdateResponse(user);
    }
}
