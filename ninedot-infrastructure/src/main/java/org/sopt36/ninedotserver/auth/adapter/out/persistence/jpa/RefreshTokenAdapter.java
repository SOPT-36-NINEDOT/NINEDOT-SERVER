package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
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
@Transactional
public class RefreshTokenAdapter implements RefreshTokenPort {

    private final RefreshTokenRepository refreshTokenJpaRepository;
    private final UserQueryPort userRepository;
    private final EntityManager entityManager;

    @Override
    public void saveOrRotate(Long userId, String plainRefreshToken, Instant expiresAt) {
        RefreshToken token = refreshTokenJpaRepository.findByUser_Id(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
                return RefreshToken.create(user, plainRefreshToken, expiresAt);
            });

        if (token.getId() != null) {
            token.rotate(plainRefreshToken, expiresAt);
        }

        refreshTokenJpaRepository.save(token);

        entityManager.flush();
    }

    @Override
    public Optional<Long> findUserIdByToken(String plainRefreshToken) {
        return refreshTokenJpaRepository
            .findByRefreshTokenAndExpiresAtAfter(plainRefreshToken, Instant.now())
            .map(RefreshToken::getUser)
            .map(User::getId);
    }

    @Override
    public void revokeByUserId(Long userId) {
        refreshTokenJpaRepository.deleteByUser_Id(userId);
    }

    @Override
    public void deleteExpired(Instant now) {
        refreshTokenJpaRepository.deleteByExpiresAtBefore(now);
    }
}
