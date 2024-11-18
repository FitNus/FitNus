package com.sparta.user.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordReset {
    private String email;
    private String code;
    private String newPassword;
}
