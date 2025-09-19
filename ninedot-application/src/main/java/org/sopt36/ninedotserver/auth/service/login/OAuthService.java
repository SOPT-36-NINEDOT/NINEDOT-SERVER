package org.sopt36.ninedotserver.auth.service.login;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.port.out.identity.IdentityProviderPort;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityToken;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuthService {

    private final IdentityProviderPort identityProviderPort;

    public ExchangeResult exchangeAuthorizationCodeAndFetchUser(
        String validatedRedirectUri,
        String authorizationCode
    ) {
        String normalizedAuthorizationCode = needsDecoding(authorizationCode)
            ? URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8)
            : authorizationCode;

        IdentityToken identityToken = identityProviderPort.exchangeAuthCode(
            normalizedAuthorizationCode, validatedRedirectUri);
        IdentityUserInfo identityUserInfo = identityProviderPort.fetchUserInfo(
            identityToken.accessToken());

        return new ExchangeResult(identityToken.accessToken(), identityUserInfo);
    }

    private boolean needsDecoding(String authorizationCode) {
        return authorizationCode != null && authorizationCode.contains("%");
    }
}
