package org.sopt36.ninedotserver.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.sopt36.ninedotserver.user.port.in.GetUserInfoUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GetUserInfoHandler implements GetUserInfoUseCase {

    private final UserQueryPort userQueryPort;

    @Override
    public UserInfoResult execute(UserInfoQuery query) {
        User user = userQueryPort.findById(query.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return UserInfoResult.from(user);
    }
}
