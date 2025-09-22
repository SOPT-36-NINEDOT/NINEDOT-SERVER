package org.sopt36.ninedotserver.auth.adapter.in.web.support;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.sopt36.ninedotserver.auth.support.CookieInstruction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieWriter {

    @Value("${cookie.domain}")
    private String cookieDomain;

    @Value("${cookie.secure}")
    private boolean secureCookie;

    @Value("${cookie.path}")
    private String cookiePath;

    @Value("${jwt.refresh-token-expiration-milliseconds}")
    private long refreshTokenExpirationMilliseconds;

    public void write(HttpServletResponse response, CookieInstruction instruction) {
        if (instruction instanceof CookieInstruction.SetRefreshToken setRefreshToken) {
            ResponseCookie responseCookie = ResponseCookie.from("refreshToken",
                    setRefreshToken.refreshToken())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("None")
                .maxAge(Duration.ofSeconds(refreshTokenExpirationMilliseconds / 1000))
                .domain(cookieDomain)
                .path(cookiePath)
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return;
        }

        if (instruction instanceof CookieInstruction.ClearRefreshToken) {
            ResponseCookie responseCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(secureCookie ? "None" : "Lax")
                .maxAge(Duration.ZERO)
                .domain(cookieDomain)
                .path(cookiePath)
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return;
        }

        throw new IllegalArgumentException(
            "Unsupported CookieInstruction type: " + instruction.getClass().getName());
    }
}
