package com.sparta.user.user.entity;

import com.sparta.common.Timestamped;
import com.sparta.user.kakao.entity.KakaoPayment;
import com.sparta.user.user.dto.request.UserRequest;
import com.sparta.common.enums.UserRole;
import com.sparta.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user")
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
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    private String bio;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KakaoPayment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons = new ArrayList<>();

    // 쿠폰 추가 메서드
    public void addCoupon(int quantity) {
        UserCoupon userCoupon = new UserCoupon(this, quantity); // UserCoupon 생성 시 this(User)를 전달
        this.userCoupons.add(userCoupon);
    }

    // 전체 쿠폰 수량 계산 (사용하지 않은 모든 쿠폰의 수량을 합산)
    public int getTotalCoupons() {
        return userCoupons.stream()
                .mapToInt(UserCoupon::getRemainingQuantity)
                .sum();
    }

    // 만료된 쿠폰의 남은 수량을 전체 수량에서 차감
    public void expireCoupons(LocalDateTime currentDate) {
        for (UserCoupon coupon : userCoupons) {
            if (coupon.getExpirationDate().isBefore(currentDate) && coupon.getRemainingQuantity() > 0) {
                coupon.expire(); // 남은 수량을 0으로 설정
            }
        }
    }

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
