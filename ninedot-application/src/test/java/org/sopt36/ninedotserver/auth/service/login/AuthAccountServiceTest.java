package org.sopt36.ninedotserver.auth.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.sopt36.ninedotserver.auth.model.OnboardingPage.ONBOARDING_COMPLETED;
import static org.sopt36.ninedotserver.auth.support.CookieInstruction.setRefreshToken;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.LoginResult;
import org.sopt36.ninedotserver.auth.dto.result.SignupResult;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.port.out.AuthProviderRepositoryPort;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;
import org.sopt36.ninedotserver.auth.service.login.dto.ExchangeResult;
import org.sopt36.ninedotserver.auth.service.login.dto.IssuedTokens;
import org.sopt36.ninedotserver.auth.service.login.dto.OnboardingStatus;
import org.sopt36.ninedotserver.auth.service.token.TokenService;
import org.sopt36.ninedotserver.auth.support.CookieInstruction;
import org.sopt36.ninedotserver.user.model.User;

@ExtendWith(MockitoExtension.class) // Mockito 기능을 JUnit 5 테스트에 활성화
class AuthAccountServiceTest {

    private AuthAccountService authAccountService;

    @Mock
    private AuthProviderRepositoryPort authProviderRepositoryPort;
    @Mock
    private TokenService tokenService;
    @Mock
    private OnboardingStatusService onboardingStatusService;

    @BeforeEach
    void setUp() {
        authAccountService = new AuthAccountService(
            authProviderRepositoryPort,
            tokenService,
            onboardingStatusService
        );
    }

    @Nested
    @DisplayName("로그인 또는 회원가입 시작 테스트")
    class LoginOrStartSignupTest {

        @Test
        @DisplayName("기존 유저가 로그인하면 LoginResult를 반환한다")
        void givenExistingUser_whenLoginOrStartSignup_thenReturnsLoginResult() {
            // given
            Long userId = 1L;
            String providerSubject = "google-12345";
            String expectedAccessToken = "test-access-token";
            String expectedRefreshTokenCookie = "test-refresh-token-cookie";

            IdentityUserInfo identityUserInfo = new IdentityUserInfo(providerSubject, "홍길동",
                "test@example.com", "picture.url");
            ExchangeResult exchangeResult = new ExchangeResult(expectedAccessToken,
                identityUserInfo);

            User mockUser = mock(User.class);
            AuthProvider mockAuthProvider = mock(AuthProvider.class);
            given(mockUser.getId()).willReturn(userId);
            given(mockAuthProvider.getUser()).willReturn(mockUser);
            given(authProviderRepositoryPort.findByProviderAndProviderUserId(ProviderType.GOOGLE,
                providerSubject))
                .willReturn(Optional.of(mockAuthProvider));

            CookieInstruction cookieInstruction = setRefreshToken(expectedRefreshTokenCookie);
            IssuedTokens issuedTokens = new IssuedTokens(expectedAccessToken, cookieInstruction);
            given(tokenService.issueTokens(userId)).willReturn(issuedTokens);

            OnboardingStatus onboardingStatus = new OnboardingStatus(true, ONBOARDING_COMPLETED);
            given(onboardingStatusService.determineOnboardingStatus(userId)).willReturn(
                onboardingStatus);

            // when
            AuthResult result = authAccountService.loginOrStartSignup(exchangeResult);

            // then
            assertThat(result).isInstanceOf(LoginResult.class);
            LoginResult loginResult = (LoginResult) result;

            assertThat(loginResult.userId()).isEqualTo(userId);
            assertThat(loginResult.accessToken()).isEqualTo(expectedAccessToken);
            assertThat(loginResult.onboardingCompleted()).isTrue();
            assertThat(loginResult.nextPage()).isEqualTo(ONBOARDING_COMPLETED);
            assertThat(loginResult.refreshTokenCookie()).contains(cookieInstruction);

            then(authProviderRepositoryPort).should()
                .findByProviderAndProviderUserId(ProviderType.GOOGLE, providerSubject);
            then(tokenService).should().issueTokens(userId);
            then(onboardingStatusService).should().determineOnboardingStatus(userId);
        }

        @Test
        @DisplayName("신규 유저가 접근하면 SignupResult를 반환한다")
        void givenNewUser_whenLoginOrStartSignup_thenReturnsSignupResult() {
            // given
            String providerSubject = "102343248876";
            String expectedAccessToken = "test-access-token";

            IdentityUserInfo identityUserInfo = new IdentityUserInfo(providerSubject,
                "new@example.com",
                "김소연", "new_picture.url");
            ExchangeResult exchangeResult = new ExchangeResult(expectedAccessToken,
                identityUserInfo);

            given(authProviderRepositoryPort
                .findByProviderAndProviderUserId(ProviderType.GOOGLE, providerSubject)
            ).willReturn(Optional.empty());

            // when
            AuthResult result = authAccountService.loginOrStartSignup(exchangeResult);

            // then
            assertThat(result).isInstanceOf(SignupResult.class);
            SignupResult signupResult = (SignupResult) result;

            assertThat(signupResult.provider()).isEqualTo(ProviderType.GOOGLE);
            assertThat(signupResult.providerUserId()).isEqualTo(providerSubject);
            assertThat(signupResult.name()).isEqualTo("김소연");
            assertThat(signupResult.email()).isEqualTo("new@example.com");
            assertThat(signupResult.picture()).isEqualTo("new_picture.url");
            assertThat(signupResult.refreshTokenCookie()).isEmpty();

            then(tokenService).should(never()).issueTokens(anyLong());
            then(onboardingStatusService).should(never()).determineOnboardingStatus(anyLong());
        }
    }
}