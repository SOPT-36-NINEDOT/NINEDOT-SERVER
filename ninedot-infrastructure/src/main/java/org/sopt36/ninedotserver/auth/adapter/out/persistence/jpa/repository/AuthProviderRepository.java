package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa.repository;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {

    Optional<AuthProvider> findByProviderAndProviderUserId(ProviderType providerType, String sub);

}
