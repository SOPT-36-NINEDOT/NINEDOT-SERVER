package org.sopt36.ninedotserver.auth.persistence;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.port.out.AuthProviderRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthProviderRepository
    extends JpaRepository<AuthProvider, Long>, AuthProviderRepositoryPort {

    Optional<AuthProvider> findByProviderAndProviderUserId(ProviderType providerType, String sub);

}
