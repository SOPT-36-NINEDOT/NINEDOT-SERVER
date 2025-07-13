package org.sopt36.ninedotserver.auth.repository;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.domain.AuthProvider;
import org.sopt36.ninedotserver.auth.domain.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthProviderRepository
    extends JpaRepository<AuthProvider, Long>, AuthProviderRepositoryCustom {

    Optional<AuthProvider> findByProviderAndProviderUserId(ProviderType providerType, String sub);
}
