package org.sopt36.ninedotserver.auth.adapter.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.port.out.token.JwtProviderPort;
import org.sopt36.ninedotserver.user.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements JwtProviderPort {

    private final UserRepositoryPort userRepository;

    @Value("${spring.jwt.secret}")
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

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token);
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

    public Authentication getAuthentication(String token) {
        Jws<Claims> jws = parseClaims(token);
        Claims claims = jws.getPayload();
        String userId = claims.getSubject();

        validateIsUser(userId);

        return new UsernamePasswordAuthenticationToken(
            userId,
            null,
            Collections.emptyList());
    }

    private void validateKeyLength(byte[] keyBytes) {
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret key must be at least 32 bytes for HS256 (current: "
                    + keyBytes.length + " bytes)"
            );
        }
    }

    private void validateIsUser(String userId) {
        if (!userRepository.existsById(Long.valueOf(userId))) {
            throw new UsernameNotFoundException("User not found: " + userId);
        }
    }
}
