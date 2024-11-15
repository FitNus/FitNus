package com.sparta.modulecommon.common.exception;


import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.kakao.exception.KakaoAccountException;
import com.sparta.modulecommon.kakao.exception.KakaoApiException;
import com.sparta.modulecommon.kakao.exception.KakaoEmailException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FitNusException.class)
    public ResponseEntity<ApiResponse<?>> handleFitNusException(FitNusException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.createError(e.getMessage(), e.getHttpStatus().value()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.createError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(
            RuntimeException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.createError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(KakaoApiException.class)
    public ApiResponse<?> handleKakaoApiException(KakaoApiException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(KakaoAccountException.class)
    public ApiResponse<?> handleKakaoAccountException(KakaoAccountException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(KakaoEmailException.class)
    public ApiResponse<?> handleKakaoEmailException(KakaoEmailException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.FORBIDDEN.value());
    }
}