package org.sopt36.ninedotserver.global.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T, M>(int code, String message, T data, M meta) {

    public ApiResponse(ErrorCode errorCode) {
        this(errorCode.getStatus().value(), errorCode.getMessage(), null, null);
    }

    public static <T> ApiResponse<T, Void> ok(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }

    public static <T, M> ApiResponse<T, M> ok(String message, T data, M meta) {
        return new ApiResponse<>(200, message, data, meta);
    }

    public static ApiResponse<Void, Void> ok(String message) {
        return new ApiResponse<>(200, message, null, null);
    }

    /**
     * Creates an API response with HTTP status 201 (Created) and the specified message.
     *
     * @param message the response message
     * @return an ApiResponse with status 201, the given message, and no data or meta information
     */
    public static ApiResponse<Void, Void> created(String message) {
        return new ApiResponse<>(201, message, null, null);
    }

    /**
     * Creates an API response with HTTP status 201 (Created), including the provided data and message.
     *
     * @param data    the response payload to include
     * @param message the message describing the creation result
     * @return an ApiResponse with status 201, the given message, the provided data, and no meta information
     */
    public static <T> ApiResponse<T, Void> created(T data, String message) {
        return new ApiResponse<>(201, message, data, null);
    }

    /**
     * Creates an error response using the specified error code and error metadata.
     *
     * @param errorCode the error code containing the HTTP status and message
     * @param meta additional error metadata to include in the response
     * @return an ApiResponse with the error status, message, and metadata, and no data payload
     */
    public static ApiResponse<Void, ErrorMeta> error(ErrorCode errorCode, ErrorMeta meta) {
        return new ApiResponse<>(errorCode.getStatus().value(), errorCode.getMessage(), null, meta);
    }

    public static ApiResponse<Void, ErrorMeta> error(int code, String message, ErrorMeta meta) {
        return new ApiResponse<>(code, message, null, meta);
    }
}
