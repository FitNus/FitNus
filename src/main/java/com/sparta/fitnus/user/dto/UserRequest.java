package com.sparta.fitnus.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Long id;
    private String email;
    private String userRole;
    private String password;
    private String nickname;
}
