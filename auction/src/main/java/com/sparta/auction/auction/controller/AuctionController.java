package com.sparta.auction.auction.controller;

import com.sparta.auction.auction.dto.AuctionRequest;
import com.sparta.auction.auction.dto.BidRequest;
import com.sparta.auction.auction.entity.Auction;
import com.sparta.auction.auction.service.AuctionService;
import com.sparta.common.apipayload.ApiResponse;
import com.sparta.common.enums.UserRole;
import com.sparta.common.user.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;

    @Secured(UserRole.Authority.ADMIN)
    @PostMapping("/create")
    public ApiResponse<Auction> createAuction(@RequestBody AuctionRequest request) {
        Auction auction = auctionService.createAuction(request.startTime(), request.endTime(), request.product());
        return ApiResponse.createSuccess(auction);
    }


    @PostMapping("/{auctionId}/bid-redis")
    public ResponseEntity<ApiResponse<?>> placeBidRedis(
            @PathVariable Long auctionId,
            @RequestBody BidRequest bidRequest,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        log.info("Received bid request: auctionId={}, userId={}, amount={}",
                auctionId, authUser.getId(), bidRequest.getBidAmount());

        try {
            // placeBidWithLock 호출
            auctionService.placeBidWithLock(auctionId, authUser.getId(), bidRequest.getBidAmount());
            return ResponseEntity.ok(ApiResponse.createSuccess("입찰이 성공적으로 처리되었습니다."));
        } catch (IllegalStateException e) {
            log.warn("Concurrent bid attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict 반환
                    .body(ApiResponse.createError("다른 사용자가 입찰 중입니다. 잠시 후 다시 시도해주세요.", HttpStatus.CONFLICT.value()));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid bid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Request 반환
                    .body(ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Bid failed due to unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.createError("서버 오류가 발생했습니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<ApiResponse<?>> placeBid(
            @PathVariable Long auctionId,
            @RequestBody BidRequest bidRequest,
            @AuthenticationPrincipal AuthUser authUser  // Spring Security 사용시
    ) {
        log.info("Received bid request: auctionId={}, userId={}, amount={}",
                auctionId, authUser.getId(), bidRequest.getBidAmount());

        try {
            auctionService.placeBid(auctionId, authUser.getId(), bidRequest.getBidAmount());
            return ResponseEntity.ok(ApiResponse.createSuccess("입찰이 성공적으로 처리되었습니다."));
        } catch (Exception e) {
            log.error("Bid failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.createError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
