package org.sopt36.ninedotserver.auth.dto.result;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.OnboardingPage;
import org.sopt36.ninedotserver.auth.support.CookieInstruction;

public record LoginResult(
    Long userId,
    String accessToken,
    boolean onboardingCompleted,
    OnboardingPage nextPage,
    Optional<CookieInstruction> refreshTokenCookie
) implements AuthResult {

}