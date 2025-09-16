package org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.BAD_MAPPING_TYPE;

import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthLoginResponse;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthResponse;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthSignupResponse;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.LoginResult;
import org.sopt36.ninedotserver.auth.dto.result.SignupResult;
import org.sopt36.ninedotserver.auth.exception.AuthException;

public class AuthResponseMapper {

    public static AuthResponse toResponse(AuthResult authResult) {
        if (authResult instanceof LoginResult loginResult) {
            return new AuthLoginResponse(
                true,
                loginResult.userId(),
                loginResult.accessToken(),
                loginResult.onboardingCompleted(),
                loginResult.nextPage()
            );
        }
        if (authResult instanceof SignupResult signupResult) {
            return new AuthSignupResponse(
                false,
                signupResult.provider().toString(),
                signupResult.providerToken(),
                signupResult.name(),
                signupResult.email(),
                signupResult.picture()
            );
        }
        throw new AuthException(BAD_MAPPING_TYPE);
    }
}

