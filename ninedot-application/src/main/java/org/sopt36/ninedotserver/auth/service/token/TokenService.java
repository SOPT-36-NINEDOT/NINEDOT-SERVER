package org.sopt36.ninedotserver.auth.service.token;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenPort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenIssuePort;
import org.sopt36.ninedotserver.auth.service.login.dto.IssuedTokens;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenIssuePort tokenIssuePort;
    private final RefreshTokenPort refreshTokenPort;

    @Value("${jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMilliseconds;

    @Value("${jwt.refresh-token-expiration-milliseconds}")
    private long refreshTokenExpirationMilliseconds;

    public IssuedTokens issueTokens(Long userId) {
        String accessToken = tokenIssuePort.createToken(userId, accessTokenExpirationMilliseconds);
        String refreshToken = tokenIssuePort.createToken(
            userId,
            refreshTokenExpirationMilliseconds
        );
        Instant refreshTokenExpiresAt = Instant.now()
            .plusMillis(refreshTokenExpirationMilliseconds);
        refreshTokenPort.saveOrRotate(userId, refreshToken, refreshTokenExpiresAt);

        return new IssuedTokens(accessToken, refreshToken);
    }
}
