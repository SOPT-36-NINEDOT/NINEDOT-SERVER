package org.sopt36.ninedotserver.auth.v1.dto.response;

import org.sopt36.ninedotserver.auth.dto.response.SignupThenLoginResult;

public record SignupThenLoginResponse(
    String accessToken,
    UserRetrievedResponse user
) {

    public static SignupThenLoginResponse from(SignupThenLoginResult signupThenLoginResult) {
        return new SignupThenLoginResponse(
            signupThenLoginResult.issuedTokens().accessToken(),
            UserRetrievedResponse.from(signupThenLoginResult.user())
        );
    }
}
