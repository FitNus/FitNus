package com.sparta.common.dto;

import com.sparta.common.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {
    private final Long id;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String email;
    private final String nickname;

    public AuthUser(Long id, UserRole role, String email, String nickname) {
        this.id = id;
        this.authorities = List.of(new SimpleGrantedAuthority((role.name())));
        this.email = email;
        this.nickname = nickname;
    }
}





