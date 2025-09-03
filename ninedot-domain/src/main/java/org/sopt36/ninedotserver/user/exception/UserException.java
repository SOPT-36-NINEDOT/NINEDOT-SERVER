package org.sopt36.ninedotserver.user.exception;


import org.sopt36.ninedotserver.exception.BusinessException;

public class UserException extends BusinessException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
