package org.sopt36.ninedotserver.auth.service.login.dto;

import org.sopt36.ninedotserver.auth.support.CookieInstruction;

public record IssuedTokens(
    String accessToken,
    CookieInstruction refreshTokenCookie
) {

}
