package org.sopt36.ninedotserver.auth.dto.result;

import org.sopt36.ninedotserver.auth.dto.token.IssuedTokens;
import org.sopt36.ninedotserver.user.model.User;

public record SignupThenLoginResult(
    IssuedTokens issuedTokens,
    UserRetrievedResult user
) {

    public static SignupThenLoginResult of(IssuedTokens issuedTokens, User user) {
        return new SignupThenLoginResult(issuedTokens, UserRetrievedResult.of(user));
    }
}
