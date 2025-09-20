package org.sopt36.ninedotserver.user.v1.dto.response;

public record UserInfoResponse(
        Long id,
        String name,
        String email,
        String profileImageUrl
) {
}
