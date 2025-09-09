package org.sopt36.ninedotserver.auth.adapter.out.google;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.GOOGLE_TOKEN_RETRIEVAL_FAILED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.GOOGLE_USER_INFO_RETRIEVAL_FAILED;

import org.sopt36.ninedotserver.auth.adapter.out.google.dto.GoogleTokenResponse;
import org.sopt36.ninedotserver.auth.adapter.out.google.dto.GoogleUserInfoResponse;
import org.sopt36.ninedotserver.auth.adapter.out.google.mapper.GoogleIdentityMapper;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.out.identity.IdentityProviderPort;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityToken;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class GoogleIdentityProviderAdapter implements IdentityProviderPort {

    private final RestClient restClient;
    private final GoogleOAuthProperties googleOAuthProperties;

    public GoogleIdentityProviderAdapter(
        @Qualifier("authRestClient") RestClient googleOAuthRestClient,
        GoogleOAuthProperties googleOAuthProperties
    ) {
        this.restClient = googleOAuthRestClient;
        this.googleOAuthProperties = googleOAuthProperties;
    }

    @Override
    public IdentityToken exchangeAuthCode(final String code, final String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", googleOAuthProperties.getClientId());
        form.add("client_secret", googleOAuthProperties.getClientSecret());
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        GoogleTokenResponse response = restClient.post()
            .uri(googleOAuthProperties.getTokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body(GoogleTokenResponse.class);

        if (response == null || response.access_token() == null) {
            throw new AuthException(GOOGLE_TOKEN_RETRIEVAL_FAILED);
        }
        return GoogleIdentityMapper.toPortToken(response);
    }

    @Override
    public IdentityUserInfo fetchUserInfo(final String accessToken) {
        GoogleUserInfoResponse response = restClient.get()
            .uri(googleOAuthProperties.getUserinfoUri())
            .headers(h -> h.setBearerAuth(accessToken))
            .retrieve()
            .body(GoogleUserInfoResponse.class);

        if (response == null || response.sub() == null) {
            throw new AuthException(GOOGLE_USER_INFO_RETRIEVAL_FAILED);
        }
        return GoogleIdentityMapper.toPortUserInfo(response);
    }
}
