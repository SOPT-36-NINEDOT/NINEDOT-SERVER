package org.sopt36.ninedotserver.auth.port.out.token;

import org.sopt36.ninedotserver.auth.dto.security.TokenClaims;

public interface TokenParsePort {

    TokenClaims parseClaims(String token);

}
