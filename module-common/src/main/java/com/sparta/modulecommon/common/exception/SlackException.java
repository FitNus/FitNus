package com.sparta.modulecommon.common.exception;

import org.springframework.http.HttpStatus;

public class SlackException extends FitNusException {

    public SlackException() {
        super("Slack 통신 과정에서 에러가 발생했습니다.", HttpStatus.BAD_GATEWAY);
    }
}
