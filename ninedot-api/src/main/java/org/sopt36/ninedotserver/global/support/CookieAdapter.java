package org.sopt36.ninedotserver.global.support;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.sopt36.ninedotserver.auth.port.CookiePort;
import org.springframework.beans.factory.annotation.Value; // @Value 임포트
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieAdapter implements CookiePort {

    @Value("${spring.cookie.domain}")
    private String cookieDomain;

    @Value("${spring.cookie.secure}")
    private boolean secureCookie;

    @Value("${spring.cookie.path}")
    private String cookiePath;

    @Value("${spring.jwt.refresh-token-expiration-milliseconds}")
    private long refreshTokenExpirationMilliseconds;

    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite("None")
            .maxAge(Duration.ofSeconds(
                refreshTokenExpirationMilliseconds / 1000))
            .domain(cookieDomain)
            .path(cookiePath)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite(secureCookie ? "None" : "Lax")
            .maxAge(Duration.ZERO)
            .domain(cookieDomain)
            .path(cookiePath)
            .build();

        response.addHeader("Set-Cookie", cookie.toString()); // setHeader 대신 addHeader 권장
    }
}
