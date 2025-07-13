package org.sopt36.ninedotserver.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.sopt36.ninedotserver.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryCustom {

    Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(String refreshToken,
        LocalDateTime now);
    void deleteByUserId(Long userId);

}
