package org.sopt36.ninedotserver.auth.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(
        String refreshToken,
        LocalDateTime now
    );

    void deleteByUserId(Long userId);

}
