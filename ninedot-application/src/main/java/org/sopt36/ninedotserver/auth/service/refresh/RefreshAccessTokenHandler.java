package org.sopt36.ninedotserver.auth.service.refresh;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.SERVICE_UNAVAILABLE;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.TOO_MANY_REQUESTS;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.dto.command.RefreshCommand;
import org.sopt36.ninedotserver.auth.dto.result.RefreshResult;
import org.sopt36.ninedotserver.auth.dto.token.IssuedTokens;
import org.sopt36.ninedotserver.auth.exception.AuthErrorCode;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.sopt36.ninedotserver.auth.port.in.RefreshAccessTokenUsecase;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenRepositoryPort;
import org.sopt36.ninedotserver.auth.service.token.TokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RefreshAccessTokenHandler implements RefreshAccessTokenUsecase {

    private static final long LOCK_WAIT_MILLIS = 200;

    private final TokenService tokenService;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final @Qualifier("refreshTokenLockCache") Cache<String, ReentrantLock> refreshTokenLockCache;

    public RefreshAccessTokenHandler(
        TokenService tokenService,
        RefreshTokenRepositoryPort refreshTokenRepositoryPort,
        @Qualifier("refreshTokenLockCache") Cache<String, ReentrantLock> refreshTokenLockCache
    ) {
        this.tokenService = tokenService;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.refreshTokenLockCache = refreshTokenLockCache;
    }

    @Transactional
    public RefreshResult execute(RefreshCommand refreshCommand) {
        ReentrantLock lock = refreshTokenLockCache.asMap()
            .computeIfAbsent(refreshCommand.refreshToken(), k -> new ReentrantLock());

        boolean acquired = false;
        try {
            acquired = lock.tryLock(LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new AuthException(TOO_MANY_REQUESTS);
            }

            RefreshToken oldRefreshToken = getValidRefreshToken(refreshCommand.refreshToken());
            Long userId = oldRefreshToken.getUserId();

            IssuedTokens issuedTokens = tokenService.issueTokens(userId);

            return RefreshResult.from(issuedTokens);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthException(SERVICE_UNAVAILABLE);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    private RefreshToken getValidRefreshToken(String refreshToken) {
        return refreshTokenRepositoryPort.findByRefreshTokenAndExpiresAtAfter(
                refreshToken,
                Instant.now()
            )
            .orElseThrow(() -> new AuthException(AuthErrorCode.EXPIRED_REFRESH_TOKEN));
    }
}
