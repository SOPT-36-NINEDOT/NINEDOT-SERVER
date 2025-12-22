package org.sopt36.ninedotserver.auth.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.sopt36.ninedotserver.auth.model.OnboardingPage.ONBOARDING_COMPLETED;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.LoginResult;
import org.sopt36.ninedotserver.auth.port.out.policy.RedirectUriValidationPort;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;

@ExtendWith(MockitoExtension.class)
class LoginOrSignupWithGoogleCodeHandlerTest {

    @Mock
    RedirectUriValidationPort redirectUriValidationPort;

    @Mock
    OAuthService oAuthService;

    @Mock
    AuthAccountService authAccountService;

    @Mock
    Cache<String, AuthResult> authResultCache;

    @InjectMocks
    LoginOrSignupWithGoogleCodeHandler loginOrSignupWithGoogleCodeHandler;

    Cache<String, ReentrantLock> realAuthCodeLockCache;

    @BeforeEach
    void setUp() {
        realAuthCodeLockCache = Caffeine.newBuilder().build();

        loginOrSignupWithGoogleCodeHandler = new LoginOrSignupWithGoogleCodeHandler(
                redirectUriValidationPort,
                oAuthService,
                authAccountService,
                realAuthCodeLockCache,
                authResultCache
        );
    }

    @Nested
    @DisplayName("execute(GoogleLoginCommand)")
    class Execute {

        @Test
        @DisplayName("검증된 redirectUri와 code로 교환/조회 후, 계정 로그인/가입을 수행하고 AuthResult를 반환한다")
        void success_flow_returnsAuthResult() {
            // given
            String code = "AUTH_CODE_123";
            String clientRedirectUri = "myapp://oauth2/callback";
            String validatedRedirectUri = "https://server.example.com/oauth2/callback";
            GoogleLoginCommand command = new GoogleLoginCommand(code, clientRedirectUri);
            ExchangeResult exchangeResult = mock(ExchangeResult.class);
            AuthResult expected = new LoginResult(
                123L, "AT.xxx.yyy", true, ONBOARDING_COMPLETED, Optional.empty()
            );


            lenient().when(authResultCache.getIfPresent(anyString())).thenReturn(null);
            lenient().when(redirectUriValidationPort.resolveAndValidate(clientRedirectUri))
                    .thenReturn(validatedRedirectUri);
            lenient().when(oAuthService.exchangeAuthorizationCodeAndFetchUser(validatedRedirectUri, code))
                    .thenReturn(exchangeResult);
            lenient().when(authAccountService.loginOrStartSignup(exchangeResult))
                    .thenReturn(expected);

            // when
            AuthResult actual = loginOrSignupWithGoogleCodeHandler.execute(command);

            // then
            assertThat(actual).isSameAs(expected);
        }

        @Test
        @DisplayName("redirectUri 검증이 실패하면 예외를 그대로 전파하고 이후 단계는 호출하지 않는다")
        void redirectValidation_fails_thenPropagates() {
            // given
            String code = "CODE";
            String badUri = "bad://uri";
            GoogleLoginCommand command = new GoogleLoginCommand(code, badUri);


            when(authResultCache.getIfPresent(anyString())).thenReturn(null);
            when(redirectUriValidationPort.resolveAndValidate(badUri))
                    .thenThrow(new IllegalArgumentException("invalid redirect"));


            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> loginOrSignupWithGoogleCodeHandler.execute(command));

            verify(redirectUriValidationPort, times(1)).resolveAndValidate(badUri);
            verifyNoInteractions(oAuthService, authAccountService);
        }

        @Test
        @DisplayName("OAuth 교환 단계에서 예외가 발생하면 예외를 전파하고 로그인/가입은 호출되지 않는다")
        void exchange_fails_thenPropagates() {
            // given
            String code = "CODE";
            String clientUri = "myapp://cb";
            String validatedUri = "https://srv/cb";
            GoogleLoginCommand command = new GoogleLoginCommand(code, clientUri);

            when(authResultCache.getIfPresent(anyString())).thenReturn(null);
            when(redirectUriValidationPort.resolveAndValidate(clientUri))
                    .thenReturn(validatedUri);
            when(oAuthService.exchangeAuthorizationCodeAndFetchUser(validatedUri, code))
                    .thenThrow(new RuntimeException("oauth error"));

            // when & then
            assertThrows(RuntimeException.class,
                () -> loginOrSignupWithGoogleCodeHandler.execute(command));

            verify(authAccountService, never()).loginOrStartSignup(any());
        }
    }
}
