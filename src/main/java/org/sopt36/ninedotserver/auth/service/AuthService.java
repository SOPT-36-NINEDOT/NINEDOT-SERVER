package org.sopt36.ninedotserver.auth.service;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.USER_NOT_FOUND;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.domain.AuthProvider;
import org.sopt36.ninedotserver.auth.domain.ProviderType;
import org.sopt36.ninedotserver.auth.domain.RefreshToken;
import org.sopt36.ninedotserver.auth.dto.response.GoogleTokenResponse;
import org.sopt36.ninedotserver.auth.dto.response.GoogleUserInfo;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.repository.AuthProviderRepository;
import org.sopt36.ninedotserver.auth.repository.RefreshTokenRepository;
import org.sopt36.ninedotserver.auth.security.JwtProvider;
import org.sopt36.ninedotserver.global.util.CookieUtil;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final RestClient restClient;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final AuthProviderRepository authProviderRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
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

    public ResponseEntity<?> loginOrSignupWithCode(String code, HttpServletResponse response) {
        GoogleTokenResponse tokens = getGoogleToken(code); //code로 구글에서 access token 가져와요
        GoogleUserInfo googleUserInfo = getGoogleUserInfo(
            tokens.accessToken()); //access token으로 user info 받아와요
        Optional<AuthProvider> optionalUser = authProviderRepository.findByProviderAndProviderUserId(
            ProviderType.GOOGLE,
            googleUserInfo.sub()); //provider user id로 AuthProvider가 db에 있는지 확인해요
        if (optionalUser.isPresent()) {
            Long userId = optionalUser.get().getUser().getId();
            String accessToken = jwtProvider.createToken(userId,
                accessTokenExpirationMilliseconds);
            String refreshToken = jwtProvider.createToken(userId,
                refreshTokenExpirationMilliseconds);
            //ㄴ토큰을 만들어
            cookieUtil.createRefreshTokenCookie(response, refreshToken);
            //ㄴ쿠키에 담아
            Map<String, Object> body = createLoginResponseBody(accessToken);
            //ㄴresponse body 생성
            addRefreshTokenToDB(userId, refreshToken);
            //ㄴdb에 refresh token 추가
            return ResponseEntity.ok(body);
        }
        Map<String, Object> body = createSignUpResponseBody(googleUserInfo);
        return ResponseEntity.ok(body);
    }

    private GoogleTokenResponse getGoogleToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");
        return restClient.post().uri("https://oauth2.googleapis.com/token")
                   .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                   .body(form).retrieve().body(GoogleTokenResponse.class);
    }

    private GoogleUserInfo getGoogleUserInfo(String accessToken) {
        return restClient.get()
                   .uri("https://openidconnect.googleapis.com/v1/userinfo")
                   .headers(h -> h.setBearerAuth(accessToken))
                   .retrieve()
                   .body(GoogleUserInfo.class);
    }

    // 로그인 시 데이터베이스에 리프레시 토큰 생성
    private void addRefreshTokenToDB(Long userId, String refreshToken) {
        Claims claims = jwtProvider.parseClaims(refreshToken);
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

    private Map<String, Object> createLoginResponseBody(String accessToken) {
        Map<String, Object> body = Map.of(
            "code", 200,
            "data", Map.of("exists", true, "accessToken", accessToken),
            "message", "성공적으로 로그인을 완료했습니다."
        );
        return body;
    }

    private Map<String, Object> createSignUpResponseBody(GoogleUserInfo googleUserInfo) {
        Map<String, Object> body = Map.of(
            "code", 200,
            "data", Map.of(
                "exists", false,
                "name", googleUserInfo.name(),
                "email", googleUserInfo.email()
            ),
            "message", "회원가입이 필요한 유저입니다."
        );
        return body;
    }
}
