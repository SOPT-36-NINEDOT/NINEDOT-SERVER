package org.sopt36.ninedotserver.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.repository.AuthProviderRepository;
import org.sopt36.ninedotserver.auth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthProviderRepository authProviderRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    
}
