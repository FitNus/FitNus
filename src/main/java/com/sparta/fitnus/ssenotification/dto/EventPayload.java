package com.sparta.fitnus.ssenotification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EventPayload {

    private String eventype;
    private String message;
    private LocalDate timestamp;

    public EventPayload(String eventype, String message, LocalDate timestamp) {
        this.eventype = eventype;
        this.message = message;
        this.timestamp = timestamp;
    }

}
