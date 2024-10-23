package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.enums.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private UserRole role;
    private UserStatus status;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.createAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
        this.role = user.getUserRole();
        this.status = user.getStatus();
    }
}
