package org.sopt36.ninedotserver.auth.service.token;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.SERVICE_UNAVAILABLE;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.TOO_MANY_REQUESTS;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.token.IssuedTokens;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenPort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenIssuePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private static final long LOCK_WAIT_MILLIS = 200;

    private final TokenIssuePort tokenIssuePort;
    private final RefreshTokenPort refreshTokenPort;

    private final Cache<Long, ReentrantLock> locks = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(30))
        .maximumSize(100_000)
        .build();

    @Value("${jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMilliseconds;

    @Value("${jwt.refresh-token-expiration-milliseconds}")
    private long refreshTokenExpirationMilliseconds;

    public IssuedTokens issueTokens(Long userId) {
        ReentrantLock lock = locks.asMap()
            .computeIfAbsent(userId, k -> new ReentrantLock(false));

        boolean acquired = false;
        try {
            acquired = lock.tryLock(LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new AuthException(TOO_MANY_REQUESTS);
            }

            String accessToken = tokenIssuePort.createToken(userId,
                accessTokenExpirationMilliseconds);
            String refreshToken = tokenIssuePort.createToken(userId,
                refreshTokenExpirationMilliseconds);
            Instant refreshTokenExpiresAt = Instant.now()
                .plusMillis(refreshTokenExpirationMilliseconds);

            refreshTokenPort.saveOrRotate(userId, refreshToken, refreshTokenExpiresAt);

            return new IssuedTokens(accessToken, refreshToken);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new AuthException(SERVICE_UNAVAILABLE);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
