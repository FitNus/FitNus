package com.sparta.fitnus.common.exception;


import com.sparta.fitnus.common.alert.slack.SlackErrorSender;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;


@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SlackErrorSender slackErrorSender;

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

    @ExceptionHandler(WrongOwnerTokenException.class)
    public ApiResponse<?> handleWrongOwnerTokenException(WrongOwnerTokenException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ClubNotFoundException.class)
    public ApiResponse<?> handleClubNotFoundException(ClubNotFoundException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(NotLeaderException.class)
    public ApiResponse<?> handleNotLeaderException(NotLeaderException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(WrongUserException.class)
    public ApiResponse<?> handleWrongUserException(WrongUserException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

//    @ExceptionHandler(TimeslotAlreadyExistsException.class)
//    public ApiResponse<?> handleTimeslotAlreadyExistsException(TimeslotAlreadyExistsException e) {
//        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
//    }

    @ExceptionHandler(FitnessNotFoundException.class)
    public ApiResponse<?> handleFitnessNotFoundException(FitnessNotFoundException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ApiResponse<?> handleScheduleNotFoundException(ScheduleNotFoundException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NotScheduleOwnerException.class)
    public ApiResponse<?> handleNotScheduleOwnerException(NotScheduleOwnerException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

//    @ExceptionHandler(TimeslotNotFoundException.class)
//    public ApiResponse<?> handleTimeslotNotFoundException(TimeslotNotFoundException e) {
//        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
//    }

    @ExceptionHandler(SlackException.class)
    public ApiResponse<?> handleSlackException(SlackException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NotAvailableTimeslot.class)
    public ApiResponse<?> NotAvailableTimeslot(NotAvailableTimeslot e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(TimeslotAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleTimeslotAlreadyExistsException(TimeslotAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(
            RuntimeException e,
            HttpServletRequest request
    ) {
        final ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        slackErrorSender.execute(cachingRequest, e);
        return ApiResponse.createError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(SseNotWorkingException.class)
    public ApiResponse<?> handleSseNotWorkingException(SseNotWorkingException e){
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NotAdminException.class)
    public ApiResponse<?> handleNotAdminException(NotAdminException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(UserBannedException.class)
    public ApiResponse<?> handleUserBannedException(UserBannedException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }
}