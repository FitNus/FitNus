package com.sparta.user.user.service;

import com.sparta.common.dto.AuthUser;
import com.sparta.common.enums.UserStatus;
import com.sparta.common.exception.NotFoundException;
import com.sparta.user.user.dto.request.ProfileUpdateRequest;
import com.sparta.user.user.dto.response.ProfileAttachFileResponse;
import com.sparta.user.user.dto.response.ProfileResponse;
import com.sparta.user.user.dto.response.ProfileUpdateResponse;
import com.sparta.user.user.entity.User;
import com.sparta.user.user.exception.ProfileUploadException;
import com.sparta.user.user.exception.UserBannedException;
import com.sparta.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
                if (existingFileName != null && !existingFileName.isEmpty()) {
                    s3Service.deleteFile(existingFileName); // s3에서 기존 파일 삭제
                }
                // 새로운 파일 업로드
                String newFileName = s3Service.uploadFile(file); // 파일 업로드 후 URL 반환
                user.addFile(newFileName);
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

    public ProfileResponse getUserProfile(Long id) {
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

        /// 바이오 업데이트
        if (request.getBio() != null && !request.getBio().isEmpty()) {
            user.updateBio(request.getBio());
        }

        // 닉네임 업데이트
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.updateNickname(request.getNickname());
        }

        userRepository.save(user);
        return new ProfileUpdateResponse(user);
    }
}
