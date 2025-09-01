package org.sopt36.ninedotserver.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.sopt36.ninedotserver.exception.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T, M>(int code, String message, T data, M meta) {

    public static <T> ApiResponse<T, Void> ok(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }

    public static ApiResponse<Void, Void> ok(String message) {
        return new ApiResponse<>(200, message, null, null);
    }

    public static ApiResponse<Void, Void> created(String message) {
        return new ApiResponse<>(201, message, null, null);
    }

    public static <T> ApiResponse<T, Void> created(T data, String message) {
        return new ApiResponse<>(201, message, data, null);
    }

    public static ApiResponse<Void, ErrorMeta> error(ErrorCode errorCode, ErrorMeta meta) {
        return new ApiResponse<>(errorCode.getStatus().value(), errorCode.getMessage(), null, meta);
    }

    public static ApiResponse<Void, ErrorMeta> error(int code, String message, ErrorMeta meta) {
        return new ApiResponse<>(code, message, null, meta);
    }
}
