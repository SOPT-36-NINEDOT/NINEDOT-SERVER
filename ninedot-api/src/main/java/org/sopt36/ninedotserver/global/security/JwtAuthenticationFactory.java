package org.sopt36.ninedotserver.global.security;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFactory {

    public Authentication getAuthentication(PrincipalDto principal) {
        return new UsernamePasswordAuthenticationToken(
            principal,
            null,
            Collections.emptyList());
    }
}
