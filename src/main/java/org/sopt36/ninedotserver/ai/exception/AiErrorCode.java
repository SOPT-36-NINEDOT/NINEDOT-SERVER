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
    CORE_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "상위목표가 존재하지 않습니다."),
    SUB_GOAL_PROMPT_PARAMETER_NOT_FOUND(HttpStatus.NOT_FOUND, "하위 목표 프롬프트 빌딩에 필요한 필수 파라미터가 누락되었습니다."),

    //500
    AI_RESPONSE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 파싱에 실패했습니다."),

    //502
    AI_API_ERROR(HttpStatus.BAD_GATEWAY, "AI API 호출 중 오류가 발생했습니다."),

    //504
    AI_API_CONNECTION_ERROR(HttpStatus.GATEWAY_TIMEOUT, "AI API와 연결할 수 없습니다.");
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
