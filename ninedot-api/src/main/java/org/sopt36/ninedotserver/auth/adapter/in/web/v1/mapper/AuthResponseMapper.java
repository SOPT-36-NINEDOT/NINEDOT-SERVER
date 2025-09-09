package org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper;

import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthLoginResponse;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthResponse;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthSignupResponse;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.LoginResult;
import org.sopt36.ninedotserver.auth.dto.result.SignupResult;

public class AuthResponseMapper {

    public static AuthResponse toResponse(AuthResult authResult) {
        if (authResult instanceof LoginResult loginResult) {
            return new AuthLoginResponse(
                loginResult.userId(),
                loginResult.accessToken(),
                loginResult.onboardingCompleted(),
                loginResult.nextPage()
            );
        }
        if (authResult instanceof SignupResult signupResult) {
            return new AuthSignupResponse(
                signupResult.provider().toString(),
                signupResult.providerToken(),
                false,
                signupResult.name(),
                signupResult.email(),
                signupResult.picture()
            );
        }
        throw new IllegalArgumentException(
            "Unsupported AuthResult type: " + authResult.getClass().getName()
        );
    }
}

