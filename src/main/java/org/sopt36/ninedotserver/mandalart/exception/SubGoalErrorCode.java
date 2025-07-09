package org.sopt36.ninedotserver.mandalart.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SubGoalErrorCode implements ErrorCode {

    // 400 BAD Request
    TITLE_NOT_BLANK(HttpStatus.BAD_REQUEST, "하위 목표는 비어있을 수 없습니다."),
    INVALID_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "하위 목표의 길이는 30자를 넘을 수 없습니다."),

    // 403 Forbidden
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 하위 목표에 접근할 권한이 없습니다."),

    // 404 Not Found
    CORE_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상위 목표입니다.");

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
