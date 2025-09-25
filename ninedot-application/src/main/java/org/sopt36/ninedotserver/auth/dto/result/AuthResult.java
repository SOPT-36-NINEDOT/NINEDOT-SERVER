package org.sopt36.ninedotserver.auth.dto.result;

import java.util.Optional;

public sealed interface AuthResult permits LoginResult, SignupResult {

    Optional<String> refreshToken();
}
