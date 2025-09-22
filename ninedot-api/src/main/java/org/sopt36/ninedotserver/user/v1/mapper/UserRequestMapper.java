package org.sopt36.ninedotserver.user.v1.mapper;

import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;

public class UserRequestMapper {

    public static UserInfoQuery toUserInfoQuery(Long userId) {
        return new UserInfoQuery(userId);
    }
}
