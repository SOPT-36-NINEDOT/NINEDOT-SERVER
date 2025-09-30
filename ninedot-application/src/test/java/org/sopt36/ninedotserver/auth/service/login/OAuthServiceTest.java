package org.sopt36.ninedotserver.auth.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.auth.port.out.identity.IdentityProviderPort;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityToken;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @Mock
    IdentityProviderPort identityProviderPort;

    @InjectMocks
    OAuthService oAuthService;

    @Nested
    @DisplayName("프론트에서 받은 코드로 구글에 사용자 정보 조회")
    class ExchangeAuthorizationCodeAndFetchUser {

        @Test
        @DisplayName("authorizationCode에 %가 없으면 그대로 exchangeAuthCode에 전달한다")
        void passThrough_whenNoEncoding() {
            // given
            String redirectUri = "https://app.example.com/callback";
            String code = "plain-code-123";

            IdentityToken token = new IdentityToken(
                "decoded-access-token",
                "decoded-refresh-token",
                1000L
            );
            IdentityUserInfo userInfo = new IdentityUserInfo(
                "sub456",
                "tester@example.com",
                "테스터",
                "http://pic2"
            );

            when(identityProviderPort.exchangeAuthCode(eq(code), eq(redirectUri)))
                .thenReturn(token);
            when(identityProviderPort.fetchUserInfo(eq(token.accessToken())))
                .thenReturn(userInfo);

            // when
            ExchangeResult result = oAuthService.exchangeAuthorizationCodeAndFetchUser(redirectUri,
                code);

            // then
            assertThat(result.identityUserInfo()).isSameAs(userInfo);

            verify(identityProviderPort).exchangeAuthCode(eq(code), eq(redirectUri));
            verify(identityProviderPort).fetchUserInfo(eq("decoded-access-token"));
        }

        @Test
        @DisplayName("authorizationCode에 %가 있으면 URLDecode 후 exchangeAuthCode에 전달한다")
        void decode_whenEncoded() {
            // given
            String redirectUri = "https://app.example.com/callback";
            String encodedCode = "abc%2F123";
            String decodedCode = "abc/123";

            IdentityToken token = new IdentityToken(
                "decoded-access-token",
                "decoded-refresh-token",
                1000L
            );
            IdentityUserInfo userInfo = new IdentityUserInfo(
                "sub456",
                "tester@example.com",
                "테스터",
                "http://pic2"
            );

            when(identityProviderPort.exchangeAuthCode(eq(decodedCode), eq(redirectUri)))
                .thenReturn(token);
            when(identityProviderPort.fetchUserInfo(eq(token.accessToken())))
                .thenReturn(userInfo);

            // when
            ExchangeResult result = oAuthService.exchangeAuthorizationCodeAndFetchUser(
                redirectUri,
                encodedCode
            );

            // then
            assertThat(result.identityUserInfo()).isSameAs(userInfo);

            verify(identityProviderPort).exchangeAuthCode(eq(decodedCode), eq(redirectUri));
            verify(identityProviderPort).fetchUserInfo(eq("decoded-access-token"));
        }
    }
}
