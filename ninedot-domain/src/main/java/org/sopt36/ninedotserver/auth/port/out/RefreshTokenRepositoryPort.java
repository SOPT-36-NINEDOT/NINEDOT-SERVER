package org.sopt36.ninedotserver.auth.port.out;

import java.time.Instant;
import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.RefreshToken;

public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(
        String refreshToken,
        Instant now
    );

    void deleteByUser_Id(Long userId);

    void delete(RefreshToken refreshToken);

    <S extends RefreshToken> S save(S refreshToken);

}
