package org.sopt36.ninedotserver.auth.service.login;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.LoginResult;
import org.sopt36.ninedotserver.auth.dto.result.SignupResult;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.port.out.AuthProviderRepositoryPort;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;
import org.sopt36.ninedotserver.auth.service.login.dto.IssuedTokens;
import org.sopt36.ninedotserver.auth.service.login.dto.OnboardingStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthAccountService {

    private final AuthProviderRepositoryPort authProviderRepositoryPort;
    private final TokenService tokenService;
    private final OnboardingStatusService onboardingStatusService;

    public AuthAccountService(
        AuthProviderRepositoryPort authProviderRepositoryPort,
        TokenService tokenService,
        OnboardingStatusService onboardingStatusService
    ) {
        this.authProviderRepositoryPort = authProviderRepositoryPort;
        this.tokenService = tokenService;
        this.onboardingStatusService = onboardingStatusService;
    }

    public AuthResult loginOrStartSignup(ExchangeResult exchangeResult) {
        String providerSubject = exchangeResult.identityUserInfo().sub();

        Optional<AuthProvider> authProviderOptional = authProviderRepositoryPort.findByProviderAndProviderUserId(
            ProviderType.GOOGLE,
            providerSubject
        );

        if (authProviderOptional.isPresent()) {
            Long userId = authProviderOptional.get().getUser().getId();

            IssuedTokens issuedTokens = tokenService.issueTokens(userId);
            OnboardingStatus onboardingStatus = onboardingStatusService.determineOnboardingStatus(
                userId);

            return new LoginResult(
                userId,
                issuedTokens.accessToken(),
                onboardingStatus.onboardingCompleted(),
                onboardingStatus.nextPage(),
                Optional.of(issuedTokens.refreshTokenCookie())
            );
        }

        return new SignupResult(
            ProviderType.GOOGLE,
            exchangeResult.identityUserInfo().sub(),
            exchangeResult.identityUserInfo().name(),
            exchangeResult.identityUserInfo().email(),
            exchangeResult.identityUserInfo().pictureUrl(),
            Optional.empty()
        );
    }
}