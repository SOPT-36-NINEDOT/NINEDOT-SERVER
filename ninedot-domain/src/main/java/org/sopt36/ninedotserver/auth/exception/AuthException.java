package org.sopt36.ninedotserver.auth.exception;

import org.sopt36.ninedotserver.exception.BusinessException;

public class AuthException extends BusinessException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
