package org.sopt36.ninedotserver.onboarding.exception;

import org.sopt36.ninedotserver.exception.BusinessException;
import org.sopt36.ninedotserver.exception.ErrorCode;

public class QuestionException extends BusinessException {

    public QuestionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
