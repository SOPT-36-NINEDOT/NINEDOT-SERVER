package org.sopt36.ninedotserver.user.exception;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 400 BAD REQUEST
    NAME_NOT_BLANK(HttpStatus.BAD_REQUEST, "이름은 빈 칸일 수 없습니다."),
    INVALID_NAME_LENGTH(HttpStatus.BAD_REQUEST, "이름은 최대 10자까지 작성 가능합니다."),

    EMAIL_NOT_BLANK(HttpStatus.BAD_REQUEST, "이메일은 빈 칸일 수 없습니다."),
    INVALID_EMAIL_LENGTH(HttpStatus.BAD_REQUEST, "이메일은 최대 255자까지 작성 가능합니다."),

    PROFILE_IMAGE_NOT_BLANK(HttpStatus.BAD_REQUEST, "프로필 사진 URL은 빈 칸일 수 없습니다."),
    PROFILE_IMAGE_URL_TOO_LONG(HttpStatus.BAD_REQUEST, "프로필 사진 URL이 너무 깁니다."),

    BIRTHDAY_NOT_BLANK(HttpStatus.BAD_REQUEST, "생년월일은 빈 칸일 수 없습니다."),
    INVALID_BIRTHDAY_LENGTH(HttpStatus.BAD_REQUEST, "생년월일은 최대 20자까지 작성 가능합니다."),
    INVALID_BIRTHDAY_TYPE(HttpStatus.BAD_REQUEST, "생년월일은 yyyy.mm.dd 형태로 작성해야 합니다."),
    BIRTHDAY_IN_FUTURE(HttpStatus.BAD_REQUEST, "생년월일은 오늘로부터 미래인 날짜가 될 수 없습니다."),

    JOB_NOT_NULL(HttpStatus.BAD_REQUEST, "직업은 반드시 선택해야 합니다."),
    INVALID_JOB_VALUE(HttpStatus.BAD_REQUEST, "직업 값이 올바르지 않습니다."),

    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");

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
