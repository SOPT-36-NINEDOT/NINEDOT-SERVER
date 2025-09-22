package org.sopt36.ninedotserver.user.dto.result;

import org.sopt36.ninedotserver.user.model.User;

public record UserInfoResult(
        Long id,
        String name,
        String email,
        String profileImageUrl
) {
    public static UserInfoResult from(User user) {
        return new UserInfoResult(
                user.getId(),
                user.nameAsString(),
                user.emailAsString(),
                user.profileImageUrlAsString()
        );
    }
}
