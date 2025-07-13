package org.sopt36.ninedotserver.user.dto.response;

import org.sopt36.ninedotserver.user.domain.User;

public record UserInfoResponse(
    Long id,
    String name,
    String email,
    String profileImageUrl
) {

    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getProfileImageUrl());
    }
}
