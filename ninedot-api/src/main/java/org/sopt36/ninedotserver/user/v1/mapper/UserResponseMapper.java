package org.sopt36.ninedotserver.user.v1.mapper;

import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.v1.dto.response.UserInfoResponse;

public class UserResponseMapper {

    public static UserInfoResponse toUserInfoResponse(UserInfoResult result) {
        return new UserInfoResponse(
                result.id(),
                result.name(),
                result.email(),
                result.profileImageUrl()
        );
    }
}
