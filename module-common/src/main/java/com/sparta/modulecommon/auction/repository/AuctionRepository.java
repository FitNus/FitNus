package com.sparta.modulecommon.auction.repository;

import com.sparta.modulecommon.auction.AuctionStatus;
import com.sparta.modulecommon.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    // 상태가 ACTIVE이고 종료 시간이 현재 시간보다 이전인 경매들을 찾는 메서드
    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus auctionStatus, LocalDateTime now);

    List<Auction> findByStatusAndStartTimeBefore(AuctionStatus auctionStatus, LocalDateTime now);

    List<Auction> findByStatus(AuctionStatus auctionStatus);
}
