package com.sparta.fitnus.user.service;

import com.sparta.fitnus.user.dto.request.UserBioRequest;
import com.sparta.fitnus.user.dto.request.UserNicknameRequest;
import com.sparta.fitnus.user.dto.response.UserBioResponse;
import com.sparta.fitnus.user.dto.response.UserGetResponse;
import com.sparta.fitnus.user.dto.response.UserNicknameResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;

    public UserGetResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        return new UserGetResponse(user.getNickname(), user.getBio());
    }

    @Transactional
    public UserBioResponse updateBio(AuthUser authUser, UserBioRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.updateBio(request.getBio());

        return UserBioResponse.entityToDto(user);
    }

    @Transactional
    public UserNicknameResponse updateNickname(AuthUser authUser, UserNicknameRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(() ->
                new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.updateNickname(request.getNickname());

        return UserNicknameResponse.entityToDto(user);
    }
}
