package com.sparta.modulecommon.user.entity;


import com.sparta.modulecommon.common.Timestamped;
import com.sparta.modulecommon.user.dto.request.UserRequest;
import com.sparta.modulecommon.user.enums.UserRole;
import com.sparta.modulecommon.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(unique = true, length = 50, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    private String bio;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // 정팩매~~
    private User(UserRequest request, UserRole role) {
        this.email = request.getEmail();
        this.userRole = role;
        this.password = request.getPassword();
        this.nickname = request.getNickname();
        this.status = UserStatus.ACTIVE;
    }

    private User(String email, String password, String nickname, UserRole role) {
        this.email = email;
        this.userRole = role;
        this.password = password;
        this.nickname = nickname;
        this.status = UserStatus.ACTIVE;
    }

    public static User of(UserRequest request, UserRole role) {
        return new User(request, role);
    }

    public static User of(String email, String password, String nickname, UserRole role) {
        return new User(email, password, nickname, role);
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addFile(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFile() {
        return this.imageUrl;
    }

    public void removeFile() {
        this.imageUrl = null;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void deactivate() {
        this.status = UserStatus.BANNED;
    }
}

