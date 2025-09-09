package org.sopt36.ninedotserver.auth.service.login;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.port.out.policy.RedirectUriValidationPort;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LoginOrSignupWithGoogleCodeHandler implements LoginOrSignupWithGoogleCodeUsecase {

    private final RedirectUriValidationPort redirectUriValidationPort;
    private final OAuthService oAuthService;
    private final AuthAccountService authAccountService;

    @Transactional
    @Override
    public AuthResult execute(GoogleLoginCommand googleLoginCommand) {
        String validatedRedirectUri = redirectUriValidationPort.resolveAndValidate(
            googleLoginCommand.clientRedirectUri()
        );
        ExchangeResult exchangeResult = oAuthService.exchangeAuthorizationCodeAndFetchUser(
            validatedRedirectUri, googleLoginCommand.code()
        );
        return authAccountService.loginOrStartSignup(exchangeResult);
    }
}
