package com.sparta.fitnus.common.apipayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "message", "statusCode", "data"})
public class ApiResponse<T> {

    @JsonProperty("success")
    private final Boolean success;

    private final String message;

    private final Integer statusCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> createSuccess(T data) {
        return new ApiResponse<>(true, SuccessStatus._OK.getMessage(), SuccessStatus._OK.getStatusCode(), data);
    }

    /**
     * 에러 응답 생성
     */
    public static <T> ApiResponse<T> createError(String message, Integer statusCode) {
        return new ApiResponse<>(false, message, statusCode, null);
    }

}



