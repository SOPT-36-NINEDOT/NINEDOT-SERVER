package org.sopt36.ninedotserver.mandalart.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CoreGoalErrorCode implements ErrorCode {

    // 400 BAD Request
    TITLE_NOT_BLANK(HttpStatus.BAD_REQUEST, "상위 목표는 비어있을 수 없습니다."),
    INVALID_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "상위 목표는 30자를 넘을 수 없습니다."),

    // 404 NOT FOUND
    CORE_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상위 목표를 찾을 수 없습니다."),

    // 409 Conflict
    CORE_GOAL_CONFLICT(HttpStatus.CONFLICT, "이미 만다라트의 해당 위치에 상위 목표가 작성되어 있습니다."),
    CORE_GOAL_COMPLETED(HttpStatus.CONFLICT, "이미 해당 만다라트의 상위 목표 8개를 모두 작성하였습니다."),
    CORE_GOAL_LIMITED(HttpStatus.CONFLICT, "상위 목표는 최대 8개까지 저장할 수 있습니다.");

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
