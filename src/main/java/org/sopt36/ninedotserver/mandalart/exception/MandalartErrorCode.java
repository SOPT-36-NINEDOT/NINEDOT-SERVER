package org.sopt36.ninedotserver.mandalart.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MandalartErrorCode implements ErrorCode {

    // 400 BAD Request
    TITLE_NOT_BLANK(HttpStatus.BAD_REQUEST, "최종 목표는 비어있을 수 없습니다."),
    INVALID_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "최종 목표는 30자를 넘을 수 없습니다."),

    // 403 FORBIDDEN
    INVALID_MANDALART_USER(HttpStatus.FORBIDDEN, "다른 유저의 만다라트를 조회할 수 없습니다."),

    // 404 NOT FOUND
    MANDALART_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 만다라트입니다.");

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
