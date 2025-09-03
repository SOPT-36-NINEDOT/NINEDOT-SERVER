package org.sopt36.ninedotserver.global.advice;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.dto.response.ErrorMeta;
import org.sopt36.ninedotserver.exception.BusinessException;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.sopt36.ninedotserver.exception.GlobalErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void, ErrorMeta>> handleBusinessException(
        BusinessException ex,
        HttpServletRequest request
    ) {
        log.error("BusinessException 발생: {}", ex.getMessage());

        ErrorCode errorCode = ex.getErrorCode();
        ErrorMeta meta = createMeta(request);

        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiResponse.error(errorCode, meta));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void, ErrorMeta>> handleMissingRequestException(
        MissingRequestHeaderException ex,
        HttpServletRequest request
    ) {
        log.error("요청 헤더 {}가 누락되었습니다.", ex.getHeaderName());

        String message = GlobalErrorCode.MISSING_HEADER.format(ex.getHeaderName());
        ErrorMeta meta = createMeta(request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message, meta));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void, ErrorMeta>> handleValidationException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
            .getAllErrors()
            .get(0)
            .getDefaultMessage();
        log.error("MethodArgumentNotValidException 발생: {}", message);

        ErrorMeta meta = createMeta(request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message, meta));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void, ErrorMeta>> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest request
    ) {
        log.error("MethodArgumentTypeMismatchException 발생: {}", ex.getMessage());
        String message = GlobalErrorCode.BAD_REQUEST.format(ex.getName());

        ErrorMeta meta = createMeta(request);

        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.getStatus())
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message, meta));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void, ErrorMeta>> handleNotFound(
        NoHandlerFoundException ex,
        HttpServletRequest request
    ) {
        log.error("NoHandlerFoundException 발생: {}", ex.getMessage());

        ErrorMeta meta = createMeta(request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(GlobalErrorCode.RESOURCE_NOT_FOUND, meta));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void, ErrorMeta>> handleUnhandledException(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("서버 내부 오류 발생: {}", ex.getMessage());

        ErrorMeta meta = createMeta(request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR, meta));
    }

    private ErrorMeta createMeta(HttpServletRequest request) {
        return new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
    }
}
