package org.sopt36.ninedotserver.ai.exception;

import org.sopt36.ninedotserver.exception.BusinessException;
import org.sopt36.ninedotserver.exception.ErrorCode;

public class AiException extends BusinessException {

    public AiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
