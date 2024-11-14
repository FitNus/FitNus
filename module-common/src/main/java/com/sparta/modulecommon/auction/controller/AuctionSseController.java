package com.sparta.modulecommon.auction.controller;

import com.sparta.modulecommon.auction.dto.BidMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionSseController {

    private final Map<Long, Set<SseEmitter>> auctionEmitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/{auctionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAuction(@PathVariable Long auctionId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        auctionEmitters.computeIfAbsent(auctionId, k -> ConcurrentHashMap.newKeySet())
                .add(emitter);

        emitter.onCompletion(() -> removeEmitter(auctionId, emitter));
        emitter.onTimeout(() -> removeEmitter(auctionId, emitter));

        return emitter;
    }

    @KafkaListener(topics = "auction-bids", groupId = "sse-group")
    public void handleBidUpdate(BidMessage bidMessage) {
        Set<SseEmitter> emitters = auctionEmitters.get(bidMessage.getAuctionId());
        if (emitters != null) {
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("bid")
                            .data(bidMessage));
                    return false;
                } catch (IOException e) {
                    return true;
                }
            });
        }
    }

    private void removeEmitter(Long auctionId, SseEmitter emitter) {
        Set<SseEmitter> emitters = auctionEmitters.get(auctionId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                auctionEmitters.remove(auctionId);
            }
        }
    }
}
