package org.sopt36.ninedotserver.auth.adapter.out.jwt;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.INVALID_TOKEN_FORMAT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.dto.security.TokenClaims;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.out.token.TokenIssuePort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenParsePort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenVerifyPort;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements TokenIssuePort, TokenParsePort, TokenVerifyPort {

    private final UserQueryPort userRepository;

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        keyBytes = Base64.getDecoder().decode(secret);
        validateKeyLength(keyBytes);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); //byte로 구성된 keybytes를 secret key 객체로 바꿈
    }

    public String createToken(Long id, long expirationMilliSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMilliSeconds);

        return Jwts.builder()
            .subject(String.valueOf(id))
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey) //jwt 3번째 부분 만들어줌
            .compact();
    }

    public TokenClaims parseClaims(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

            Claims claims = jws.getPayload();

            long userId;
            try {
                userId = Long.parseLong(claims.getSubject());
            } catch (NumberFormatException e) {
                throw new AuthException(INVALID_TOKEN_FORMAT, e.getMessage());
            }

            return new TokenClaims(
                userId,
                claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant(),
                claims.getExpiration() == null ? null : claims.getExpiration().toInstant()
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(INVALID_TOKEN_FORMAT, e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private void validateKeyLength(byte[] keyBytes) {
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret key must be at least 32 bytes for HS256 (current: "
                    + keyBytes.length + " bytes)"
            );
        }
    }
}
