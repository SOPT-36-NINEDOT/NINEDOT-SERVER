package org.sopt36.ninedotserver.onboarding.exception;

import org.sopt36.ninedotserver.global.exception.BusinessException;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

public class AnswerException extends BusinessException {

    public AnswerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
