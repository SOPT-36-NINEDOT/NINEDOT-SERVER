package org.sopt36.ninedotserver.auth.service.login;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.SERVICE_UNAVAILABLE;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.TOO_MANY_REQUESTS;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.port.out.policy.RedirectUriValidationPort;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginOrSignupWithGoogleCodeHandler implements LoginOrSignupWithGoogleCodeUsecase {

    private static final long LOCK_WAIT_MILLIS = 200;

    private final RedirectUriValidationPort redirectUriValidationPort;
    private final OAuthService oAuthService;
    private final AuthAccountService authAccountService;
    private final @Qualifier("authCodeLockCache") Cache<String, ReentrantLock> authCodeLockCache;

    public LoginOrSignupWithGoogleCodeHandler(
        RedirectUriValidationPort redirectUriValidationPort,
        OAuthService oAuthService,
        AuthAccountService authAccountService,
        @Qualifier("authCodeLockCache") Cache<String, ReentrantLock> authCodeLockCache
    ) {
        this.redirectUriValidationPort = redirectUriValidationPort;
        this.oAuthService = oAuthService;
        this.authAccountService = authAccountService;
        this.authCodeLockCache = authCodeLockCache;
    }

    @Transactional
    @Override
    public AuthResult execute(GoogleLoginCommand googleLoginCommand) {
        ReentrantLock lock = authCodeLockCache.asMap()
            .computeIfAbsent(googleLoginCommand.code(), k -> new ReentrantLock());

        boolean acquired = false;
        try {
            acquired = lock.tryLock(LOCK_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new AuthException(TOO_MANY_REQUESTS);
            }

            String validatedRedirectUri = redirectUriValidationPort.resolveAndValidate(
                googleLoginCommand.clientRedirectUri()
            );
            ExchangeResult exchangeResult = oAuthService.exchangeAuthorizationCodeAndFetchUser(
                validatedRedirectUri, googleLoginCommand.code()
            );

            return authAccountService.loginOrStartSignup(exchangeResult);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthException(SERVICE_UNAVAILABLE);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}