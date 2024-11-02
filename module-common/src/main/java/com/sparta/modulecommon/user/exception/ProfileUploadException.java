package com.sparta.modulecommon.user.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ProfileUploadException extends FitNusException {

    public ProfileUploadException() {
        super("업로드 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
    }
}