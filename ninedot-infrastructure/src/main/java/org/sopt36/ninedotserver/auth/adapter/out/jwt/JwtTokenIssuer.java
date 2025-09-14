package org.sopt36.ninedotserver.auth.adapter.out.jwt;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.port.out.token.TokenIssuePort;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenIssuer implements TokenIssuePort {

    private final JwtProvider jwtProvider;

    @Override
    public String issueAccessToken(Long userId, long expiresMs) {
        return jwtProvider.createToken(userId, expiresMs);
    }
}