package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa.repository.RefreshTokenRepository;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRepository jpa;

    @Override
    public Optional<RefreshToken> findByRefreshTokenAndExpiresAtAfter(
        String refreshToken,
        Instant now
    ) {
        return jpa.findByRefreshTokenAndExpiresAtAfter(refreshToken, now);
    }

    @Override
    public void deleteByUser_Id(Long userId) {
        jpa.deleteByUser_Id(userId);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        jpa.delete(refreshToken);
    }

    @Override
    public <S extends RefreshToken> S save(S refreshToken) {
        return jpa.save(refreshToken);
    }
}
