package org.sopt36.ninedotserver.ai.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {
    //404
    MANDALART_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 만다라트입니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문이 존재하지 않습니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 답변이 존재하지 않습니다."),

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
