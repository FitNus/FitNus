package com.sparta.modulecommon.auction.event;

import com.sparta.modulecommon.auction.dto.BidMessage;
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
