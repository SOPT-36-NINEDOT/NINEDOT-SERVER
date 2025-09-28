package org.sopt36.ninedotserver.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 400
    BAD_MAPPING_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 내부 DTO 매핑 타입입니다."),
    INVALID_TOKEN_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 토큰 형식입니다."),

    //401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다. 다시 로그인하세요."),

    //404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),

    // 429
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "토큰 발급이 진행 중입니다. 잠시 후 다시 시도해주세요."),

    //500
    GOOGLE_USER_INFO_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "구글 사용자 정보 조회에 실패했습니다."),
    GOOGLE_TOKEN_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "구글 토큰 발급에 실패했습니다."),

    // 503
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "요청이 지연되었습니다. 다시 시도해주세요.");

    @Getter
    private final HttpStatus status;
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
