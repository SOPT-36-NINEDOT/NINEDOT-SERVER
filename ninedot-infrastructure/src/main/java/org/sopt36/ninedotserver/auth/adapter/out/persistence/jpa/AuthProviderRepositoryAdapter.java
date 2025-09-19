package org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.adapter.out.persistence.jpa.repository.AuthProviderRepository;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.port.out.AuthProviderRepositoryPort;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class AuthProviderRepositoryAdapter implements AuthProviderRepositoryPort {

    private final AuthProviderRepository jpa;

    @Override
    public Optional<AuthProvider> findByProviderAndProviderUserId(
        ProviderType providerType,
        String sub
    ) {
        return jpa.findByProviderAndProviderUserId(providerType, sub);
    }

    @Override
    public <S extends AuthProvider> S save(S authProvider) {
        return jpa.save(authProvider);
    }
}
