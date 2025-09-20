package org.sopt36.ninedotserver.user.v1.mapper;

import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.v1.dto.response.UserInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper {

    public UserInfoResponse toResponse(UserInfoResult result) {
        return new UserInfoResponse(
                result.id(),
                result.name(),
                result.email(),
                result.profileImageUrl()
        );
    }
}
