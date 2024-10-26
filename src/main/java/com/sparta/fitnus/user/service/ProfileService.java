package com.sparta.fitnus.user.service;

import com.sparta.fitnus.common.service.S3Service;
import com.sparta.fitnus.user.dto.request.UserBioRequest;
import com.sparta.fitnus.user.dto.request.UserNicknameRequest;
import com.sparta.fitnus.user.dto.response.UserAttachFileResponse;
import com.sparta.fitnus.user.dto.response.UserBioResponse;
import com.sparta.fitnus.user.dto.response.UserGetResponse;
import com.sparta.fitnus.user.dto.response.UserNicknameResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
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
    public UserAttachFileResponse attachFile(AuthUser authUser, MultipartFile file)
            throws IOException {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (file != null && !file.isEmpty()) {
            String fileName = s3Service.uploadFile(file); // 파일 업로드 후 URL 반환

            user.addFile(fileName);
        }

        userRepository.save(user);

        return new UserAttachFileResponse(user);
    }

    public UserGetResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        return new UserGetResponse(user.getNickname(), user.getBio(), user.getImageUrl());
    }

    @Transactional
    public UserBioResponse updateBio(AuthUser authUser, UserBioRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.updateBio(request.getBio());

        return new UserBioResponse(user);
    }

    @Transactional
    public UserNicknameResponse updateNickname(AuthUser authUser, UserNicknameRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.updateNickname(request.getNickname());

        return new UserNicknameResponse(user);
    }
}
