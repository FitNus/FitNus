package com.sparta.modulecommon.auction.controller;

import com.sparta.modulecommon.auction.dto.AuctionRequest;
import com.sparta.modulecommon.auction.dto.BidRequest;
import com.sparta.modulecommon.auction.entity.Auction;
import com.sparta.modulecommon.auction.service.AuctionService;
import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping("/create")
    public ApiResponse<Auction> createAuction(@RequestBody AuctionRequest request) {
        Auction auction = auctionService.createAuction(request.startTime(), request.endTime());
        return ApiResponse.createSuccess(auction);
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
