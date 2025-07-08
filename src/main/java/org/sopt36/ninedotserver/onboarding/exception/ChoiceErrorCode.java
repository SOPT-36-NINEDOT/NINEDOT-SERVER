package org.sopt36.ninedotserver.onboarding.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChoiceErrorCode implements ErrorCode {

    // TODO 에러 코드 별 메시지 관리. 추후 구현 시 아래 세미콜론 삭제
    ;

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
