package org.sopt36.ninedotserver.mandalart.exception;

import org.sopt36.ninedotserver.exception.BusinessException;
import org.sopt36.ninedotserver.exception.ErrorCode;

public class SubGoalException extends BusinessException {

    public SubGoalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
