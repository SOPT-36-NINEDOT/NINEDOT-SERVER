package org.sopt36.ninedotserver.user.dto.response;

import org.sopt36.ninedotserver.user.model.User;

public record UserInfoResponse(
        Long id,
        String name,
        String email,
        String profileImageUrl
) {

    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.nameAsString(),
                user.emailAsString(),
                user.profileImageUrlAsString()
        );
    }
}
