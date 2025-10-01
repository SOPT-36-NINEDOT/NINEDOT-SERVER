package org.sopt36.ninedotserver.auth.service.login;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.concurrent.locks.ReentrantLock;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.port.out.policy.RedirectUriValidationPort;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LoginOrSignupWithGoogleCodeHandler implements LoginOrSignupWithGoogleCodeUsecase {

    private final RedirectUriValidationPort redirectUriValidationPort;
    private final OAuthService oAuthService;
    private final AuthAccountService authAccountService;
    private final @Qualifier("authCodeLockCache") Cache<String, ReentrantLock> authCodeLockCache;
    private final @Qualifier("authResultCache") Cache<String, AuthResult> authResultCache;

    public LoginOrSignupWithGoogleCodeHandler(
        RedirectUriValidationPort redirectUriValidationPort,
        OAuthService oAuthService,
        AuthAccountService authAccountService,
        @Qualifier("authCodeLockCache") Cache<String, ReentrantLock> authCodeLockCache,
        @Qualifier("authResultCache") Cache<String, AuthResult> authResultCache
    ) {
        this.redirectUriValidationPort = redirectUriValidationPort;
        this.oAuthService = oAuthService;
        this.authAccountService = authAccountService;
        this.authCodeLockCache = authCodeLockCache;
        this.authResultCache = authResultCache;
    }

    @Override
    public AuthResult execute(GoogleLoginCommand googleLoginCommand) {
        String authCode = googleLoginCommand.code();

        AuthResult cachedResult = authResultCache.getIfPresent(authCode);
        if (cachedResult != null) {
            return cachedResult;
        }

        ReentrantLock lock = authCodeLockCache.get(authCode, k -> new ReentrantLock());

        lock.lock();
        try {
            cachedResult = authResultCache.getIfPresent(authCode);
            if (cachedResult != null) {
                return cachedResult;
            }

            String validatedRedirectUri = redirectUriValidationPort.resolveAndValidate(
                googleLoginCommand.clientRedirectUri()
            );
            ExchangeResult exchangeResult = oAuthService.exchangeAuthorizationCodeAndFetchUser(
                validatedRedirectUri, authCode
            );

            AuthResult result = authAccountService.loginOrStartSignup(exchangeResult);

            authResultCache.put(authCode, result);

            return result;

        } finally {
            try {
                lock.unlock();
            } finally {
                authCodeLockCache.asMap().remove(authCode, lock);
            }
        }
    }
}
