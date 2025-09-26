package org.sopt36.ninedotserver.user.model.value;

import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import java.util.regex.Pattern;

public record Email(String value) {

    public static final int MAX_LENGTH = 255;
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");

    public Email {
        validateNotBlank(value);
        validateLength(value);
        validateFormat(value);
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_LENGTH);
        }
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new UserException(UserErrorCode.EMAIL_NOT_BLANK);
        }
    }

    private static void validateFormat(String value) {
        if (!EMAIL_REGEX.matcher(value).matches()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
