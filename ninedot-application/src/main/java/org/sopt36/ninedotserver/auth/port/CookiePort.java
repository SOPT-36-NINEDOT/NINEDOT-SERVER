package org.sopt36.ninedotserver.auth.port;

import jakarta.servlet.http.HttpServletResponse;

public interface CookiePort {

    void createRefreshTokenCookie(HttpServletResponse response, String refreshToken);

    void clearRefreshTokenCookie(HttpServletResponse response);
}
