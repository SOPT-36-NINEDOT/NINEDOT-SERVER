package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa.repository;

import java.time.Instant;
import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser_Id(Long userId);

    Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(
        String refreshToken,
        Instant now
    );

    void deleteByUser_Id(Long userId);

    void deleteByExpiresAtBefore(Instant cutoff);
}
