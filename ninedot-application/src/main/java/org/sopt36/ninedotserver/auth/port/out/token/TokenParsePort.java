package org.sopt36.ninedotserver.auth.port.out.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenParsePort {

    Jws<Claims> parseClaims(String token);

}
