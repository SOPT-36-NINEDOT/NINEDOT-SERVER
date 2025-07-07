package org.sopt36.ninedotserver.mandalart.exception;

import org.sopt36.ninedotserver.global.exception.BusinessException;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

public class CoreGoalException extends BusinessException {

    public CoreGoalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
