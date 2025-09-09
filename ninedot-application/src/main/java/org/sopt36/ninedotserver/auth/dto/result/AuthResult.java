package org.sopt36.ninedotserver.auth.dto.result;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.support.CookieInstruction;

public sealed interface AuthResult permits LoginResult, SignupResult {

    Optional<CookieInstruction> refreshTokenCookie();
}
