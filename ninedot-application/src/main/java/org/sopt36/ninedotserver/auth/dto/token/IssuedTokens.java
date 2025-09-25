package org.sopt36.ninedotserver.auth.dto.token;

public record IssuedTokens(
    String accessToken,
    String refreshToken
) {

}
