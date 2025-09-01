package org.sopt36.ninedotserver.onboarding.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChoiceErrorCode implements ErrorCode {

    // 404 NOT FOUND
    CHOICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "등록되지 않은 선택지 id입니다.");

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
