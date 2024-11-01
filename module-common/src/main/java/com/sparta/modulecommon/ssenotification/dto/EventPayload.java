package com.sparta.modulecommon.ssenotification.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class EventPayload {
    private final Long id;
    private final String eventype;
    private final String message;
    private final LocalDateTime timestamp;

    public EventPayload(Long id, String eventype, String message, LocalDateTime timestamp ){
        this.id = id;
        this.eventype = eventype;
        this.message = message;
        this.timestamp = timestamp;
    }
}
