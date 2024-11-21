package com.sparta.auction.auction.entity;

import com.sparta.auction.auction.AuctionStatus;
import com.sparta.common.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
@Slf4j
@Table(name = "auction")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "highest_bid", nullable = false)
    private int highestBid = 0;

    @Column(nullable = false)
    private String product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id")
    private User highestBidder;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.PENDING;

    public Auction(LocalDateTime startTime, LocalDateTime endTime, String product) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.product = product;
    }

    public boolean isAuctionOpen() {
        return LocalDateTime.now().isBefore(endTime);
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Checking auction activity - now: {}, startTime: {}, endTime: {}, status: {}",
                now, startTime, endTime, status);

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime nowZdt = now.atZone(zoneId);
        ZonedDateTime startZdt = startTime.atZone(zoneId);
        ZonedDateTime endZdt = endTime.atZone(zoneId);

        boolean isTimeValid = nowZdt.isAfter(startZdt) && nowZdt.isBefore(endZdt);
        boolean isStatusValid = status == AuctionStatus.ACTIVE;

        log.info("Time check: {}, Status check: {}", isTimeValid, isStatusValid);

        return isTimeValid && isStatusValid;
    }

    public void placeBid(User user, int amount) {  // Long에서 int로 변경
        if (this.status != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("경매가 활성화 상태가 아닙니다.");
        }

        if (amount > this.highestBid) {
            this.highestBid = amount;
            this.highestBidder = user;
        } else {
            throw new IllegalArgumentException("현재 최고 입찰가보다 높은 금액을 입력해주세요.");
        }
    }

    public void finishAuction() {
        if (status != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("진행중인 경매가 아닙니다.");
        }
        status = AuctionStatus.FINISHED;
    }

    public void start() {
        LocalDateTime now = LocalDateTime.now();
        if (status != AuctionStatus.PENDING) {
            throw new IllegalStateException("경매가 이미 시작되었거나 종료되었습니다.");
        }
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            this.status = AuctionStatus.ACTIVE;
        }
    }

    public void setStatus(AuctionStatus auctionStatus) {
        this.status = auctionStatus;
    }
}