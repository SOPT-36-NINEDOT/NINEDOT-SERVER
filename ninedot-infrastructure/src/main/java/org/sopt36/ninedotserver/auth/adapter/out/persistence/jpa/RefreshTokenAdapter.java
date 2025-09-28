package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa.repository.RefreshTokenRepository;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenPort;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RefreshTokenAdapter implements RefreshTokenPort {

    private final RefreshTokenRepository refreshTokenJpaRepository;
    private final UserQueryPort userRepository;
    private final EntityManager entityManager;

    @Override
    public void saveOrRotate(Long userId, String plainRefreshToken, Instant expiresAt) {
        LocalDateTime expirationUtc = toLocalDateTimeUtc(expiresAt);

        RefreshToken token = refreshTokenJpaRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
                return RefreshToken.create(user, plainRefreshToken, expirationUtc);
            });

        if (token.getId() != null) {
            token.rotate(plainRefreshToken, expirationUtc);
        }

        refreshTokenJpaRepository.save(token);

        entityManager.flush();
    }

    @Override
    public Optional<Long> findUserIdByToken(String plainRefreshToken) {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        return refreshTokenJpaRepository
            .findByRefreshTokenAndExpiresAtAfter(plainRefreshToken, nowUtc)
            .map(RefreshToken::getUser)
            .map(User::getId);
    }

    @Override
    public void revokeByUserId(Long userId) {
        refreshTokenJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteExpired(Instant now) {
        refreshTokenJpaRepository.deleteByExpiresAtBefore(toLocalDateTimeUtc(now));
    }

    private LocalDateTime toLocalDateTimeUtc(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
