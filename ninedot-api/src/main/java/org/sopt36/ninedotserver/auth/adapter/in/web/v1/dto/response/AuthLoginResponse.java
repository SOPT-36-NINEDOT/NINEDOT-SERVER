package org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response;

import org.sopt36.ninedotserver.auth.model.OnboardingPage;

public record AuthLoginResponse(
    Long userId,
    String accessToken,
    boolean onboardingCompleted,
    OnboardingPage onboardingPage
) implements AuthResponse {

}
