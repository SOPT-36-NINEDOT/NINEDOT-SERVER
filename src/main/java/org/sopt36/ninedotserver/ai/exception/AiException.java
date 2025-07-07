package org.sopt36.ninedotserver.ai.exception;

import org.sopt36.ninedotserver.global.exception.BusinessException;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

public class AiException extends BusinessException {

    public AiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
