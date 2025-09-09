package org.sopt36.ninedotserver.auth.port.out;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;

public interface JwtProviderPort {

    String createToken(Long id, long expirationMilliSeconds);

    Jws<Claims> parseClaims(String token);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);
}
