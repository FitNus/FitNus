package com.sparta.fitnus.common.exception;

public class ChangeSamePasswordException extends RuntimeException {
    public ChangeSamePasswordException() {
        super("새 비밀번호는 이전 비밀번호와 같을 수 없습니다.");
    }
}
