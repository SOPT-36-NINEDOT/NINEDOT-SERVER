package org.sopt36.ninedotserver.user.model.value;

import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

public record ProfileImageUrl(String value) {

    public static final int MAX_LENGTH = 1000;

    public ProfileImageUrl {
        validateNotBlank(value);
        validateLength(value);
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new UserException(UserErrorCode.PROFILE_IMAGE_URL_TOO_LONG);
        }
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new UserException(UserErrorCode.PROFILE_IMAGE_NOT_BLANK);
        }
    }

    @Override
    public String toString() {
        return value;
    }


}
