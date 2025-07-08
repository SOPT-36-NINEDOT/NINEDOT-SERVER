package org.sopt36.ninedotserver.mandalart.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MandalartErrorCode implements ErrorCode {

    TITLE_NOT_BLANK(HttpStatus.BAD_REQUEST, "최종 목표는 비어있을 수 없습니다."),
    INVALID_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "최종 목표는 30자를 넘을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
