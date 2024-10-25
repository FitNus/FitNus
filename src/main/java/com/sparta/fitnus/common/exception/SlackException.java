package com.sparta.fitnus.common.exception;

public class SlackException extends RuntimeException {

    public SlackException() {
        super("Slack 통신 과정에서 에러가 발생했습니다.");
    }
}
