package org.sopt36.ninedotserver.mandalart.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum HistoryErrorCode implements ErrorCode {

    // 404 Not Found
    HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘 해당 하위 목표를 완료하지 않았습니다."),

    // 409 Conflict
    HISTORY_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 오늘 해당 하위 목표를 완료했습니다.");

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
