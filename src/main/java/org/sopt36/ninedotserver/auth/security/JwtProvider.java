package org.sopt36.ninedotserver.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${JWT_SECRET_KEY}")
    private String secret;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(Long id, long expirationMilliSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMilliSeconds);

        return Jwts.builder()
                   .setSubject(String.valueOf(id))
                   .setIssuedAt(now)
                   .setExpiration(expiry)
                   .signWith(secretKey, SignatureAlgorithm.HS256)
                   .compact();
    }

    public Claims parseClaims(String token) {
        Jws<Claims> jws = Jwts.parser()
                              .setSigningKey(secretKey)
                              .build()
                              .parseClaimsJws(token);
        return jws.getBody();
    }
}
