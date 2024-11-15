package com.sparta.modulecommon.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\p{Punct}])[A-Za-z\\d\\p{Punct}]{8,20}$",
            message = "비밀번호는 8자에서 20자 사이여야 하며, 최소한 하나의 문자, 하나의 숫자, 그리고 하나의 특수 문자를 포함해야 합니다.")
    private String oldPassword;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\p{Punct}])[A-Za-z\\d\\p{Punct}]{8,20}$",
            message = "비밀번호는 8자에서 20자 사이여야 하며, 최소한 하나의 문자, 하나의 숫자, 그리고 하나의 특수 문자를 포함해야 합니다.")
    private String newPassword;
}