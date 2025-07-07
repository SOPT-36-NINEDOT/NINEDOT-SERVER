package org.sopt36.ninedotserver.auth.exception;

import org.sopt36.ninedotserver.global.exception.BusinessException;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
