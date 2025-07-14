package org.sopt36.ninedotserver.auth.service;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.GOOGLE_TOKEN_RETRIEVAL_FAILED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.GOOGLE_USER_INFO_RETRIEVAL_FAILED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.INVALID_REFRESH_TOKEN;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.UNAUTHORIZED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.USER_NOT_FOUND;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.domain.AuthProvider;
import org.sopt36.ninedotserver.auth.domain.OnboardingPage;
import org.sopt36.ninedotserver.auth.domain.ProviderType;
import org.sopt36.ninedotserver.auth.domain.RefreshToken;
import org.sopt36.ninedotserver.auth.dto.response.GoogleTokenResponse;
import org.sopt36.ninedotserver.auth.dto.response.GoogleUserInfo;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse.LoginData;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse.SignupData;
import org.sopt36.ninedotserver.auth.dto.response.NewAccessTokenResponse;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.repository.AuthProviderRepository;
import org.sopt36.ninedotserver.auth.repository.RefreshTokenRepository;
import org.sopt36.ninedotserver.global.config.security.JwtProvider;
import org.sopt36.ninedotserver.global.util.CookieUtil;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final RestClient restClient;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final AuthProviderRepository authProviderRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;
    @Value("${GOOGLE_CLIENT_ID}")
    String clientId;
    @Value("${GOOGLE_CLIENT_SECRET}")
    String clientSecret;
    @Value("${GOOGLE_REDIRECT_URI}")
    String redirectUri;
    @Value("${spring.jwt.access-token-expiration-milliseconds}")
    long accessTokenExpirationMilliseconds;
    @Value("${spring.jwt.refresh-token-expiration-milliseconds}")
    long refreshTokenExpirationMilliseconds;

    @SuppressWarnings("unchecked")
    public <T> LoginOrSignupResponse<T> loginOrSignupWithCode(String code,
        HttpServletResponse response) {
        GoogleTokenResponse tokens = getGoogleToken(code); //code로 구글에서 access token 가져와요
        GoogleUserInfo googleUserInfo = getGoogleUserInfo(
            tokens.accessToken()); //access token으로 user info 받아와요
        Optional<AuthProvider> optionalUser = authProviderRepository.findByProviderAndProviderUserId(
            ProviderType.GOOGLE,
            googleUserInfo.sub()); //provider user id로 AuthProvider가 db에 있는지 확인해요

        log.info(googleUserInfo.sub());
        if (optionalUser.isPresent()) {
            Long userId = optionalUser.get().getUser().getId();
            String accessToken = jwtProvider.createToken(userId,
                accessTokenExpirationMilliseconds);
            generateAndStoreRefreshToken(userId, response);
            return (LoginOrSignupResponse<T>) createLoginResponse(userId, accessToken);
        }
        return (LoginOrSignupResponse<T>) createSignupResponse(googleUserInfo);
    }

    public NewAccessTokenResponse createNewAccessToken(String refreshToken,
        HttpServletResponse response) {

        RefreshToken rt = isRefreshTokenValid(refreshToken);

        Long userId = rt.getUser().getId();
        String newAccessToken = jwtProvider.createToken(userId, accessTokenExpirationMilliseconds);

        refreshTokenRepository.delete(rt);
        generateAndStoreRefreshToken(userId, response);
        return new NewAccessTokenResponse(newAccessToken, "새로운 액세스토큰이 생성되었습니다.");
    }

    @Transactional
    public void deleteRefreshToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        refreshTokenRepository.deleteByUserId(userId);
    }

    private GoogleTokenResponse getGoogleToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");
        try {
            GoogleTokenResponse response = restClient.post()
                                               .uri("https://oauth2.googleapis.com/token")
                                               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                               .body(form)
                                               .retrieve()
                                               .body(GoogleTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new AuthException(GOOGLE_TOKEN_RETRIEVAL_FAILED);
            }

            return response;
        } catch (Exception e) {
            throw new AuthException(GOOGLE_TOKEN_RETRIEVAL_FAILED, e.getMessage());
        }
    }

    private GoogleUserInfo getGoogleUserInfo(String accessToken) {
        return restClient.get()
                   .uri("https://openidconnect.googleapis.com/v1/userinfo")
                   .headers(h -> h.setBearerAuth(accessToken))
                   .retrieve()
                   //파라미터 누락, 존재하지 않는 사용자, 비활성 사용자(구글에서) 등 처리
                   .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                       throw new AuthException(
                           UNAUTHORIZED,
                           "Google userinfo 4xx error: " + readErrorBody(res)
                       );
                   })
                   //구글 터짐
                   .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                       throw new AuthException(
                           GOOGLE_USER_INFO_RETRIEVAL_FAILED,
                           "Google userinfo 5xx error: " + readErrorBody(res)
                       );
                   })
                   .body(GoogleUserInfo.class);
    }

    private void generateAndStoreRefreshToken(Long userId, HttpServletResponse response) {
        String refreshToken = jwtProvider.createToken(userId,
            refreshTokenExpirationMilliseconds);
        //ㄴ토큰을 만들어
        cookieUtil.createRefreshTokenCookie(response, refreshToken);
        //ㄴ쿠키에 담아
        addRefreshTokenToDB(userId, refreshToken);
        //ㄴdb에 refresh token 추가
    }

    private RefreshToken isRefreshTokenValid(String refreshToken) {
        return refreshTokenRepository
                   .findByRefreshTokenAndExpiresAtAfter(refreshToken, LocalDateTime.now())
                   .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
    }

    // 로그인 시 데이터베이스에 리프레시 토큰 생성
    private void addRefreshTokenToDB(Long userId, String refreshToken) {
        Claims claims = jwtProvider.parseClaims(refreshToken).getPayload();
        User user = userRepository.findById(userId)
                        .orElseThrow(
                            () -> new AuthException(USER_NOT_FOUND));
        refreshTokenRepository.save(
            RefreshToken.create(user, refreshToken,
                expirationDateToLocalDateTime(claims.getExpiration())));
    }

    private LocalDateTime expirationDateToLocalDateTime(Date expirationDate) {
        return expirationDate.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    private LoginOrSignupResponse<LoginData> createLoginResponse(Long userId, String accessToken) {
        Boolean onboardingCompleted = findUserOnboardingCompleted(userId);
        OnboardingPage onboardingPage = findUserOnboardingPage(userId);
        LoginData loginData = new LoginData(true, accessToken, onboardingCompleted, onboardingPage,
            "성공적으로 로그인을 완료했습니다.");
        return new LoginOrSignupResponse<>(loginData);
    }

    private LoginOrSignupResponse<SignupData> createSignupResponse(GoogleUserInfo googleUserInfo) {
        SignupData signupData = new SignupData(ProviderType.GOOGLE.toString(), googleUserInfo.sub(),
            false, googleUserInfo.name(),
            googleUserInfo.email(), "회원가입이 필요한 유저입니다.");
        return new LoginOrSignupResponse<>(signupData);
    }

    private String readErrorBody(ClientHttpResponse res) {
        try (var is = res.getBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "(error reading body: " + e.getMessage() + ")";
        }
    }

    private boolean findUserOnboardingCompleted(Long userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isPresent()) {
            return optUser.get().getOnboardingCompleted();
        }
        throw new AuthException(USER_NOT_FOUND);
    }

    private OnboardingPage findUserOnboardingPage(Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new AuthException(USER_NOT_FOUND));
        if (user.getOnboardingCompleted()) {
            return OnboardingPage.ONBOARDING_COMPLETED;
        }
        if (coreGoalRepository.existsByUserId(userId)) {
            return OnboardingPage.SUB_GOAL;
        }
        if (mandalartRepository.existsByUserId(userId)) {
            return OnboardingPage.CORE_GOAL;
        }
        return OnboardingPage.MANDALART;
    }
}
