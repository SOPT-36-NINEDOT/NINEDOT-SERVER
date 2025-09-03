package org.sopt36.ninedotserver.auth.dto.response;

import org.sopt36.ninedotserver.user.model.User;

public record SignupResponse(
    String accessToken,
    UserRetrievedResponse user
) {

    public static SignupResponse of(String accessToken, User user) {
        return new SignupResponse(accessToken, UserRetrievedResponse.of(user));
    }
}
