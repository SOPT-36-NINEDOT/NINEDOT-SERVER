package org.sopt36.ninedotserver.user.port.in;

import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;

public interface GetUserInfoUseCase {
    UserInfoResult execute(UserInfoQuery query);
}
