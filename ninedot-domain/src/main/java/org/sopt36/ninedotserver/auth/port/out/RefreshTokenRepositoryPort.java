package org.sopt36.ninedotserver.auth.port.out;

import java.time.LocalDateTime;
import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.RefreshToken;

public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(
        String refreshToken,
        LocalDateTime now
    );

    void deleteByUserId(Long userId);

    void delete(RefreshToken refreshToken);

    <S extends RefreshToken> S save(S refreshToken);

}
