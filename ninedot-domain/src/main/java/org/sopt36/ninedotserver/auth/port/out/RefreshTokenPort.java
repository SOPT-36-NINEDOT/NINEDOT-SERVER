package org.sopt36.ninedotserver.auth.port.out;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenPort {

    void saveOrRotate(Long userId, String plainRefreshToken, Instant expiresAt);

    Optional<Long> findUserIdByToken(String plainRefreshToken);

    void revokeByUserId(Long userId);

    void deleteExpired(Instant now);
}
