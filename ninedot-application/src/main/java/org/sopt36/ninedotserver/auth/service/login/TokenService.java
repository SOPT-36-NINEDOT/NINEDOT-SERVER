package org.sopt36.ninedotserver.auth.service.login;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenPort;
import org.sopt36.ninedotserver.auth.port.out.token.JwtProviderPort;
import org.sopt36.ninedotserver.auth.service.login.dto.IssuedTokens;
import org.sopt36.ninedotserver.auth.support.CookieInstruction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProviderPort jwtProviderPort;
    private final RefreshTokenPort refreshTokenPort;

    @Value("${spring.jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMilliseconds;

    @Value("${spring.jwt.refresh-token-expiration-milliseconds}")
    private long refreshTokenExpirationMilliseconds;

    public IssuedTokens issueTokens(Long userId) {
        String accessToken = jwtProviderPort.createToken(userId, accessTokenExpirationMilliseconds);

        String refreshToken = jwtProviderPort.createToken(userId,
            refreshTokenExpirationMilliseconds);
        Instant refreshTokenExpiresAt = Instant.now()
            .plusMillis(refreshTokenExpirationMilliseconds);
        refreshTokenPort.saveOrRotate(userId, refreshToken, refreshTokenExpiresAt);

        CookieInstruction refreshTokenCookieInstruction = CookieInstruction.setRefreshToken(
            refreshToken);
        return new IssuedTokens(accessToken, refreshTokenCookieInstruction);
    }

    public CookieInstruction clearRefreshTokenInstruction() {
        return CookieInstruction.clearRefreshToken();
    }
}
