package com.sparta.fitnus.user.service;

import com.sparta.fitnus.user.dto.request.UserBioRequest;
import com.sparta.fitnus.user.dto.response.UserBioResponse;
import com.sparta.fitnus.user.dto.response.UserGetResponse;
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
                new IllegalArgumentException("User with id " + id + " not found"));

        return new UserGetResponse(user.getNickname(), user.getBio());
    }

    @Transactional
    public UserBioResponse updateBio(Long id, UserBioRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found"));

        user.updateBio(request.getBio());

        return UserBioResponse.entityToDto(user);
    }
}
