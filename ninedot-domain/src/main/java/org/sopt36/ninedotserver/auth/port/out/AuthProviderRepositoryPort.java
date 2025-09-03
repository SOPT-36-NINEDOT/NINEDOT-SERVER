package org.sopt36.ninedotserver.auth.port.out;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;

public interface AuthProviderRepositoryPort {

    Optional<AuthProvider> findByProviderAndProviderUserId(ProviderType providerType, String sub);

    <S extends AuthProvider> S save(S authProvider);
}
