package com.sparta.fitnus.common.exception;


import com.sparta.fitnus.common.apipayload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChangeSamePasswordException.class)
    public ApiResponse<?> handleChangeSamePasswordException(ChangeSamePasswordException e, HttpServletRequest request) {
        request.getRequestURI();
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ApiResponse<?> handleDuplicateNameException(DuplicateNameException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ApiResponse<?> handleDuplicateEmailException(DuplicateEmailException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NoSignedUserException.class)
    public ApiResponse<?> handleNoSignedUserException(NoSignedUserException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ApiResponse<?> handleWrongPasswordException(WrongPasswordException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<?> handleNullPointerException(NullPointerException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<?> handleNotFoundException(NotFoundException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(WrongAdminTokenException.class)
    public ApiResponse<?> handleWrongAdminTokenException(WrongAdminTokenException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }
}

