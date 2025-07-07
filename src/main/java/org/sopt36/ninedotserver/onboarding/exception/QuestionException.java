package org.sopt36.ninedotserver.onboarding.exception;

import org.sopt36.ninedotserver.global.exception.BusinessException;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

public class QuestionException extends BusinessException {

    public QuestionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
