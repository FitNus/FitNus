package com.sparta.auction.auction.event;

import com.sparta.auction.auction.dto.BidMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BidUpdateEvent extends ApplicationEvent {
    private final BidMessage bidMessage;

    public BidUpdateEvent(Object source, BidMessage bidMessage) {
        super(source);
        this.bidMessage = bidMessage;
    }
}
