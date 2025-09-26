package org.sopt36.ninedotserver.auth.v1.dto.response;

import org.sopt36.ninedotserver.auth.model.OnboardingPage;

public record AuthLoginResponse(
    boolean exists,
    Long userId,
    String accessToken,
    boolean onboardingCompleted,
    OnboardingPage onboardingPage
) implements AuthResponse {

}
