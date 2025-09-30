package org.sopt36.ninedotserver.auth.dto.security;

import java.time.Instant;

public record TokenClaims(
    Long userId,
    Instant issuedAt,
    Instant expiresAt
) {

}
