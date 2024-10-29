package com.sparta.fitnus.ssenotification.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class EventPayload {

    private String eventype;
    private String message;
    private LocalDateTime timestamp;

    public EventPayload(String eventype, String message, LocalDateTime timestamp ){
        this.eventype = eventype;
        this.message = message;
        this.timestamp = timestamp;
    }

}
