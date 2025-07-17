package org.sopt36.ninedotserver.auth.service;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.GOOGLE_TOKEN_RETRIEVAL_FAILED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.GOOGLE_USER_INFO_RETRIEVAL_FAILED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.INVALID_REFRESH_TOKEN;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.UNAUTHORIZED;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.USER_NOT_FOUND;
import static org.sopt36.ninedotserver.onboarding.exception.QuestionErrorCode.QUESTION_NOT_FOUND;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.domain.AuthProvider;
import org.sopt36.ninedotserver.auth.domain.OnboardingPage;
import org.sopt36.ninedotserver.auth.domain.ProviderType;
import org.sopt36.ninedotserver.auth.domain.RefreshToken;
import org.sopt36.ninedotserver.auth.dto.request.SignupRequest;
import org.sopt36.ninedotserver.auth.dto.response.GoogleTokenResponse;
import org.sopt36.ninedotserver.auth.dto.response.GoogleUserInfo;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse.LoginData;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse.SignupData;
import org.sopt36.ninedotserver.auth.dto.response.NewAccessTokenResponse;
import org.sopt36.ninedotserver.auth.dto.response.SignupResponse;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.repository.AuthProviderRepository;
import org.sopt36.ninedotserver.auth.repository.RefreshTokenRepository;
import org.sopt36.ninedotserver.global.config.security.JwtProvider;
import org.sopt36.ninedotserver.global.util.CookieUtil;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.onboarding.domain.Answer;
import org.sopt36.ninedotserver.onboarding.domain.ChoiceInfo;
import org.sopt36.ninedotserver.onboarding.domain.Question;
import org.sopt36.ninedotserver.onboarding.exception.QuestionException;
import org.sopt36.ninedotserver.onboarding.repository.AnswerRepository;
import org.sopt36.ninedotserver.onboarding.repository.QuestionRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
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

    public AuthService(
        @Qualifier("authRestClient") RestClient restClient,
        JwtProvider jwtProvider,
        CookieUtil cookieUtil,
        AuthProviderRepository authProviderRepository,
        RefreshTokenRepository refreshTokenRepository,
        UserRepository userRepository,
        CoreGoalRepository coreGoalRepository,
        MandalartRepository mandalartRepository,
        AnswerRepository answerRepository,
        QuestionRepository questionRepository
    ) {
        this.restClient = restClient;
        this.jwtProvider = jwtProvider;
        this.cookieUtil = cookieUtil;
        this.authProviderRepository = authProviderRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.coreGoalRepository = coreGoalRepository;
        this.mandalartRepository = mandalartRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

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

    @Transactional
    public SignupResponse registerUser(SignupRequest request, HttpServletResponse response) {
        User user = User.create(
            request.name(),
            request.email(),
            request.profileImageUrl(),
            request.birthday(),
            request.job()
        );
        userRepository.save(user);

        List<Answer> answers = getAnswers(request, user);
        answerRepository.saveAll(answers);

        AuthProvider authProvider = AuthProvider.create(
            user,
            ProviderType.valueOf(request.socialProvider().toUpperCase()),
            request.socialToken()
        );
        authProviderRepository.save(authProvider);

        String accessToken = jwtProvider
                                 .createToken(user.getId(), accessTokenExpirationMilliseconds);
        generateAndStoreRefreshToken(user.getId(), response);

        return SignupResponse.of(accessToken, user);
    }

    private GoogleTokenResponse getGoogleToken(String code) {
        log.info("Google 토큰 요청 시작: code={}, redirectUri={}", code, redirectUri);
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
            log.info("Google 응답 수신: {}", response);
            if (response == null) {
                log.warn("Google 응답이 null입니다.");
                throw new AuthException(GOOGLE_TOKEN_RETRIEVAL_FAILED);
            }
            if (response.accessToken() == null) {
                log.warn("Google 응답에서 accessToken이 null입니다: {}", response);
                throw new AuthException(GOOGLE_TOKEN_RETRIEVAL_FAILED);
            }
            return response;
        } catch (Exception e) {
            log.error("Google 토큰 요청 실패", e);
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
        SignupData signupData = new SignupData("GOOGLE", googleUserInfo.sub(), false,
            googleUserInfo.name(),
            googleUserInfo.email(),
            googleUserInfo.profileImageUrl(),
            "회원가입이 필요한 유저입니다.");
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


    private List<Answer> getAnswers(SignupRequest request, User user) {
        return request.answers().stream()
                   .map(answer -> {
                       Question question = questionRepository.
                                               findById(answer.questionId())
                                               .orElseThrow(
                                                   () -> new QuestionException(
                                                       QUESTION_NOT_FOUND
                                                   ));
                       String content = ChoiceInfo.getShortSentenceById(
                           answer.choiceId());

                       return Answer.create(question, user, content);
                   })
                   .collect(Collectors.toList());
    }
}
