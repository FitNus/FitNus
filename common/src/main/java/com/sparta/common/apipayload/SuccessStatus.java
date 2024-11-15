package com.sparta.common.apipayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    OK(HttpStatus.OK, 200, "OK");

    private final HttpStatus httpStatus;
    private final Integer statusCode;
    private final String message;

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .statusCode(statusCode)
                .message(message)
                .httpStatus(httpStatus)
                .success(true)
                .build();
    }
}
