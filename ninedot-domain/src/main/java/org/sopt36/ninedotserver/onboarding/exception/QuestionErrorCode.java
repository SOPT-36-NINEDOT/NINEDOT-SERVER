package org.sopt36.ninedotserver.onboarding.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum QuestionErrorCode implements ErrorCode {

    // 400 BAD REQUEST
    CONTENT_NOT_BLANK(HttpStatus.BAD_REQUEST, "질문 내용은 빈 칸일 수 없습니다."),
    INVALID_CONTENT_LENGTH(HttpStatus.BAD_REQUEST, "질문 내용은 255자까지 가능합니다."),

    // 404 NOT FOUND
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문 데이터가 존재하지 않습니다."),
    JOB_DROPDOWN_NOT_FOUND(HttpStatus.NOT_FOUND, "직업 목록이 존재하지 않습니다.");

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
