package org.sopt36.ninedotserver.auth.dto.result;

import org.sopt36.ninedotserver.auth.dto.token.IssuedTokens;

public record RefreshResult(
    String accessToken,
    String refreshToken
) {

    public static RefreshResult from(IssuedTokens issuedTokens) {
        return new RefreshResult(issuedTokens.accessToken(), issuedTokens.refreshToken());
    }
}
