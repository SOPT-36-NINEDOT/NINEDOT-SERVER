package org.sopt36.ninedotserver.auth.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.sopt36.ninedotserver.auth.model.OnboardingPage.ONBOARDING_COMPLETED;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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

    @InjectMocks
    LoginOrSignupWithGoogleCodeHandler loginOrSignupWithGoogleCodeHandler;

    @Nested
    @DisplayName("execute(GoogleLoginCommand)")
    class Execute {

        @Test
        @DisplayName("검증된 redirectUri와 code로 교환/조회 후, 계정 로그인/가입을 수행하고 AuthResult를 반환한다")
        void success_flow_returnsAuthResult() {
            // given
            String clientRedirectUri = "myapp://oauth2/callback";
            String validatedRedirectUri = "https://server.example.com/oauth2/callback";
            String code = "AUTH_CODE_123";

            GoogleLoginCommand command = new GoogleLoginCommand(code, clientRedirectUri);

            ExchangeResult exchangeResult = mock(ExchangeResult.class);
            AuthResult expected = new LoginResult(
                123L,
                "AT.xxx.yyy",
                true,
                ONBOARDING_COMPLETED,
                Optional.empty()
            );

            when(redirectUriValidationPort.resolveAndValidate(clientRedirectUri))
                .thenReturn(validatedRedirectUri);
            when(oAuthService.exchangeAuthorizationCodeAndFetchUser(validatedRedirectUri, code))
                .thenReturn(exchangeResult);
            when(authAccountService.loginOrStartSignup(exchangeResult))
                .thenReturn(expected);

            // when
            AuthResult actual = loginOrSignupWithGoogleCodeHandler.execute(command);

            // then
            assertThat(actual).isSameAs(expected);

            InOrder inOrder = inOrder(redirectUriValidationPort, oAuthService, authAccountService);
            inOrder.verify(redirectUriValidationPort).resolveAndValidate(eq(clientRedirectUri));
            inOrder.verify(oAuthService).exchangeAuthorizationCodeAndFetchUser(
                eq(validatedRedirectUri), eq(code));
            inOrder.verify(authAccountService).loginOrStartSignup(eq(exchangeResult));
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        @DisplayName("redirectUri 검증이 실패하면 예외를 그대로 전파하고 이후 단계는 호출하지 않는다")
        void redirectValidation_fails_thenPropagates() {
            // given
            GoogleLoginCommand command = new GoogleLoginCommand("CODE", "bad://uri");
            when(redirectUriValidationPort.resolveAndValidate("bad://uri"))
                .thenThrow(new IllegalArgumentException("invalid redirect"));

            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> loginOrSignupWithGoogleCodeHandler.execute(command));

            verify(redirectUriValidationPort, times(1)).resolveAndValidate("bad://uri");
            verifyNoInteractions(oAuthService, authAccountService);
        }

        @Test
        @DisplayName("OAuth 교환 단계에서 예외가 발생하면 예외를 전파하고 로그인/가입은 호출되지 않는다")
        void exchange_fails_thenPropagates() {
            // given
            GoogleLoginCommand command = new GoogleLoginCommand("CODE", "myapp://cb");
            when(redirectUriValidationPort.resolveAndValidate("myapp://cb"))
                .thenReturn("https://srv/cb");
            when(oAuthService.exchangeAuthorizationCodeAndFetchUser("https://srv/cb", "CODE"))
                .thenThrow(new RuntimeException("oauth error"));

            // when & then
            assertThrows(RuntimeException.class,
                () -> loginOrSignupWithGoogleCodeHandler.execute(command));

            verify(authAccountService, never()).loginOrStartSignup(any());
        }
    }
}