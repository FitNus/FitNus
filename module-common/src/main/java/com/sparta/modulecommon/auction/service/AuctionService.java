package com.sparta.modulecommon.auction.service;

import com.sparta.modulecommon.auction.AuctionStatus;
import com.sparta.modulecommon.auction.dto.AuctionResultMessage;
import com.sparta.modulecommon.auction.dto.BidMessage;
import com.sparta.modulecommon.auction.entity.Auction;
import com.sparta.modulecommon.auction.repository.AuctionRepository;
import com.sparta.modulecommon.user.entity.User;
import com.sparta.modulecommon.user.repository.UserRepository;
import com.sparta.modulecommon.user.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(
            topics = "auction-bids",
            groupId = "auction-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleBidMessage(BidMessage bidMessage, Acknowledgment ack) {
        log.info("Received bid message from Kafka: auctionId={}, bidderId={}, amount={}",
                bidMessage.getAuctionId(),
                bidMessage.getBidderId(),
                bidMessage.getBidAmount());

        try {
            placeBid(bidMessage.getAuctionId(), bidMessage.getBidderId(), bidMessage.getBidAmount());
            ack.acknowledge();  // 수동 커밋 수행
            log.info("Successfully processed bid message and acknowledged.");
        } catch (Exception e) {
            log.error("Failed to process bid message from Kafka", e);
            // 실패 시 커밋하지 않음
        }
    }


    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3)
    public void placeBid(Long auctionId, Long userId, int bidAmount) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경매입니다."));

        log.info("Attempting to place bid - auctionId: {}, userId: {}, amount: {}",
                auctionId, userId, bidAmount);

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("경매가 활성화 상태가 아닙니다.");
        }

        User user = userRepository.findUserById(userId);
//        int userCouponBalance = user.getTotalCoupons();
//        if (userCouponBalance < bidAmount) {
//            throw new IllegalArgumentException("사용한 쿠폰 수량이 부족합니다. 현재 남은 수량: " + userCouponBalance);
//        }
        if (auction.getHighestBid() != 0 && bidAmount <= auction.getHighestBid()) {
            throw new IllegalArgumentException("현재 최고 입찰가보다 높은 금액을 입력해주세요.");
        }

        auction.placeBid(user, bidAmount);
        auctionRepository.save(auction);
        log.info("Bid placed successfully");

        // 입찰 성공 후 Kafka에 메시지 전송
        sendBidMessage(auctionId, userId, bidAmount);
    }

    // Kafka에 입찰 메시지 전송 메서드
    private void sendBidMessage(Long auctionId, Long bidderId, int bidAmount) {
        BidMessage bidMessage = new BidMessage(auctionId, bidderId, bidAmount);
        kafkaTemplate.send("auction-bids", bidMessage);
        log.info("Sent bid message to Kafka: auctionId={}, bidderId={}, amount={}", auctionId, bidderId, bidAmount);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndFinishAuctions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Checking auctions for finish at {}", now);  // 로그 추가

        List<Auction> auctions = auctionRepository.findByStatus(AuctionStatus.ACTIVE);

        for (Auction auction : auctions) {
            // 시작 시간이 지났는지 먼저 확인
            if (now.isAfter(auction.getStartTime())) {
                if (now.isBefore(auction.getEndTime())) {
                    // 시작 시간은 지났지만 종료 시간은 아직 안된 경우
                    auction.setStatus(AuctionStatus.ACTIVE);
                    log.info("Auction {} activated at {}", auction.getId(), now);
                } else {
                    // 종료 시간도 지난 경우
                    finishAuction(auction);
                    log.info("Auction {} finished at {}", auction.getId(), now);
                }
                auctionRepository.save(auction);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndStartAuctions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Checking pending auctions at {}", now);  // 로그 추가

        List<Auction> pendingAuctions = auctionRepository
                .findByStatus(AuctionStatus.PENDING);  // 모든 PENDING 상태의 경매를 가져옴

        for (Auction auction : pendingAuctions) {
            log.info("Checking auction {}: startTime={}, now={}",
                    auction.getId(), auction.getStartTime(), now);  // 로그 추가

            if (now.isAfter(auction.getStartTime())) {
                auction.setStatus(AuctionStatus.ACTIVE);  // 직접 상태 변경
                auctionRepository.save(auction);
                log.info("Auction {} started", auction.getId());
            }
        }
    }

    private void finishAuction(Auction auction) {
        String lockKey = "auction-finish:" + auction.getId();
        try {
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));

            if (Boolean.FALSE.equals(acquired)) {
                return;
            }

            auction.finishAuction();  // 경매 종료 로직, 내부에서 상태 변경 필요

            // 최고 입찰자가 있는 경우에만 쿠폰 사용 처리
            if (auction.getHighestBidder() != null && auction.getHighestBid() > 0) {
                couponService.useCoupons(
                        auction.getHighestBidder().getId(),
                        Math.toIntExact(auction.getHighestBid())
                );

                // 경매 종료 이벤트 발행
                kafkaTemplate.send("auction-results",
                        new AuctionResultMessage(
                                auction.getId(),
                                auction.getHighestBidder().getId(),
                                auction.getHighestBid()
                        )
                );
            } else {
                // 입찰자가 없는 경우의 처리
                log.info("Auction {} finished without any bids", auction.getId());
                kafkaTemplate.send("auction-results",
                        new AuctionResultMessage(
                                auction.getId(),
                                null,
                                0
                        )
                );
            }

            // 경매 상태를 명확히 FINISHED로 설정
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);

        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    public Auction createAuction(LocalDateTime startTime, LocalDateTime endTime) {
        Auction auction = new Auction(startTime, endTime);
        return auctionRepository.save(auction);
    }
}
