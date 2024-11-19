package com.sparta.auction.auction.service;

import com.sparta.auction.auction.AuctionStatus;
import com.sparta.auction.auction.dto.AuctionResultMessage;
import com.sparta.auction.auction.dto.BidMessage;
import com.sparta.auction.auction.entity.Auction;
import com.sparta.auction.auction.repository.AuctionRepository;
import com.sparta.common.config.CouponService;
import com.sparta.common.user.entity.User;
import com.sparta.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

        int userCouponBalance = user.getTotalCoupons();
        if (userCouponBalance < bidAmount) {
            throw new IllegalArgumentException("사용한 쿠폰 수량이 부족합니다. 현재 남은 수량: " + userCouponBalance);
        }

        if (auction.getHighestBid() != 0 && bidAmount <= auction.getHighestBid()) {
            throw new IllegalArgumentException("현재 최고 입찰가보다 높은 금액을 입력해주세요.");
        }

        auction.placeBid(user, bidAmount);
        auctionRepository.save(auction);
        log.info("Bid placed successfully");

        // 입찰 성공 후 Kafka에 메시지 전송
        sendBidMessage(auctionId, userId, bidAmount);
    }

    public void sendBidMessage(Long auctionId, Long bidderId, int bidAmount) {
        int highestBid = getHighestBidForAuction(auctionId); // 최고 입찰가 조회 메서드 호출
        BidMessage bidMessage = new BidMessage(auctionId, bidderId, bidAmount, highestBid);
        kafkaTemplate.send("auction-bids", bidMessage);
        log.info("Sent bid message to Kafka: auctionId={}, bidderId={}, amount={}, highestBid={}",
                auctionId, bidderId, bidAmount, highestBid);
    }

    private int getHighestBidForAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경매입니다."));
        return auction.getHighestBid();
    }

    @Scheduled(fixedRate = 3600000) //한시간마다
    @Transactional
    public void checkAndUpdateAuctions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Checking auctions for start or finish at {}", now);

        // PENDING 상태 경매 확인 및 시작 처리
        List<Auction> pendingAuctions = auctionRepository.findByStatus(AuctionStatus.PENDING);
        for (Auction auction : pendingAuctions) {
            log.info("Checking pending auction {}: startTime={}, now={}", auction.getId(), auction.getStartTime(), now);
            if (now.isAfter(auction.getStartTime())) {
                auction.setStatus(AuctionStatus.ACTIVE);
                auctionRepository.save(auction);
                log.info("Auction {} started", auction.getId());
            }
        }

        // ACTIVE 상태 경매 확인 및 종료 처리
        List<Auction> activeAuctions = auctionRepository.findByStatus(AuctionStatus.ACTIVE);
        for (Auction auction : activeAuctions) {
            log.info("Checking active auction {}: endTime={}, now={}", auction.getId(), auction.getEndTime(), now);
            if (now.isAfter(auction.getEndTime())) {
                finishAuction(auction);
                log.info("Auction {} finished", auction.getId());
                auctionRepository.save(auction);
            }
        }
    }

    /**
     * 경매를 종료하는 메서드로, Redis를 이용한 분산 잠금 메커니즘을 사용하여
     * 동시에 여러 프로세스가 종료 작업을 수행하는 것을 방지.
     *
     * @param auction 종료할 경매 객체
     */
    private void finishAuction(Auction auction) {
        // Redis에 잠금 키 설정을 위한 고유한 키 생성
        String lockKey = "auction-finish:" + auction.getId();
        try {
            // 잠금 시도 및 성공 시 lockKey에 "locked" 값을 10초 동안 설정하고, 실패 시 작업을 종료합니다.
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

            // 경매 상태를 명확히 FINISHED로 설정 & DB저장
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);

        } finally {
            // 경매 종료 작업 완료 후 Redis에서 잠금 키를 삭제하여 잠금을 해제
            redisTemplate.delete(lockKey);
        }
    }

    public Auction createAuction(LocalDateTime startTime, LocalDateTime endTime, String product) {
        Auction auction = new Auction(startTime, endTime, product);
        return auctionRepository.save(auction);
    }

    @Transactional
    public void placeBidWithLock(Long auctionId, Long userId, int bidAmount) {
        String lockKey = "auction:bid:" + auctionId;
        String lockValue = UUID.randomUUID().toString(); // 소유권 확인용 값

        boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(5));

        if (!acquired) {
            throw new IllegalStateException("입찰 중 다른 트랜잭션이 이미 진행 중입니다.");
        }

        try {
            // placeBid 메서드가 @Transactional 환경에서 호출되어야 함
            placeBid(auctionId, userId, bidAmount);

            // 트랜잭션 종료 후 락 해제
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            String currentValue = redisTemplate.opsForValue().get(lockKey);
                            if (lockValue.equals(currentValue)) {
                                redisTemplate.delete(lockKey);
                            }
                        }
                    }
            );
        } catch (Exception e) {
            String currentValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
            }
            throw e;
        }
    }
}
