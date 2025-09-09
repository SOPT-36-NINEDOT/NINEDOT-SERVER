package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.sopt36.ninedotserver.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(
        String refreshToken,
        LocalDateTime now
    );

    void deleteByUserId(Long userId);

    Long user(User user);
}
