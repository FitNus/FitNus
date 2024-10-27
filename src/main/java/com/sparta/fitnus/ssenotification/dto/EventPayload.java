package com.sparta.fitnus.ssenotification.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class EventPayload {

    private String eventype;
    private String message;
    private LocalDate timestamp;

    public EventPayload(String eventype, String message, LocalDate timestamp ){
        this.eventype = eventype;
        this.message = message;
        this.timestamp = timestamp;
    }

}
