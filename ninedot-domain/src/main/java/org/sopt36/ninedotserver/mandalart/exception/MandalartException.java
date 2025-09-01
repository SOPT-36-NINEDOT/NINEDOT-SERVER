package org.sopt36.ninedotserver.mandalart.exception;

import org.sopt36.ninedotserver.exception.BusinessException;
import org.sopt36.ninedotserver.exception.ErrorCode;

public class MandalartException extends BusinessException {

    public MandalartException(ErrorCode errorCode) {
        super(errorCode);
    }
}
