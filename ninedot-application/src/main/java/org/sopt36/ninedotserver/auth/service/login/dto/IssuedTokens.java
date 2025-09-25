package org.sopt36.ninedotserver.auth.service.login.dto;

public record IssuedTokens(
    String accessToken,
    String refreshToken
) {

}
