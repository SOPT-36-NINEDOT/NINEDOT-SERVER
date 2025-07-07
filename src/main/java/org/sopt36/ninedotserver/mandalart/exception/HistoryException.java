package org.sopt36.ninedotserver.mandalart.exception;

import org.sopt36.ninedotserver.global.exception.BusinessException;
import org.sopt36.ninedotserver.global.exception.ErrorCode;

public class HistoryException extends BusinessException {

    public HistoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
