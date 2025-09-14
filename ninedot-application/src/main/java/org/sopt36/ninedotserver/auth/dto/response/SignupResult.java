package org.sopt36.ninedotserver.auth.dto.response;

import org.sopt36.ninedotserver.user.model.User;

public record SignupResult(
    String accessToken,
    UserRetrievedResult user
) {

    public static SignupResult of(String accessToken, User user) {
        return new SignupResult(accessToken, UserRetrievedResult.of(user));
    }
}
