package org.sopt36.ninedotserver.onboarding.exception;

import org.sopt36.ninedotserver.exception.BusinessException;
import org.sopt36.ninedotserver.exception.ErrorCode;

public class ChoiceException extends BusinessException {

    public ChoiceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
