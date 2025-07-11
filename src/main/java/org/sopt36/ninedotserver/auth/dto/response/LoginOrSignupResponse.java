package org.sopt36.ninedotserver.auth.dto.response;

import org.sopt36.ninedotserver.auth.domain.OnboardingPage;

public record LoginOrSignupResponse<T>(
    int code,
    T data,
    String message
) {

    public record LoginData(
        boolean exists,
        String accessToken,
        Boolean onboardingCompleted,
        OnboardingPage onboardingPage
    ) {

    }

    public record SignupData(
        boolean exists,
        String name,
        String email
    ) {

    }
}
