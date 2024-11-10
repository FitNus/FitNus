package com.sparta.modulecommon.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int usedQuantity = 0;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    public UserCoupon(User user, int quantity) {
        this.user = user;  // user를 직접 설정
        this.quantity = quantity;
        this.purchaseDate = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plusMinutes(1); // 테스트용 1분 유효기간
    }

    public int getRemainingQuantity() {
        return quantity - usedQuantity;
    }

    public void useCoupon(int amount) {
        if (usedQuantity + amount <= quantity) {
            usedQuantity += amount;
        } else {
            throw new IllegalArgumentException("사용할 수 있는 쿠폰 수량을 초과했습니다.");
        }
    }

    public void expire() {
        this.quantity = usedQuantity; // 남은 수량을 0으로 설정
    }
}

