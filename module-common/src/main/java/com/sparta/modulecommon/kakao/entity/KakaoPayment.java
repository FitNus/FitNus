package com.sparta.modulecommon.kakao.entity;


import com.sparta.modulecommon.common.Timestamped;
import com.sparta.modulecommon.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "kakaoPayment")
public class KakaoPayment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String tid;  // 카카오페이 결제 고유 ID


    @Column(nullable = false)
    private int amount;  // 결제 금액

    @Column(nullable = false)
    private String status; // 결제 상태 (예: COMPLETED, FAILED 등)

    @Column(nullable = false)
    private int coupon;

    public KakaoPayment(User user, String tid, int amount, String status, int coupon) {
        this.user = user;
        this.tid = tid;
        this.amount = amount;
        this.status = status;
        this.coupon = coupon;
    }
}
