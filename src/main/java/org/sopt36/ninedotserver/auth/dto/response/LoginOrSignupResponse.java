package org.sopt36.ninedotserver.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import org.sopt36.ninedotserver.auth.domain.OnboardingPage;

public record LoginOrSignupResponse<T>(
    @JsonValue T data
) {

    public record LoginData(
        boolean exists,
        String accessToken,
        Boolean onboardingCompleted,
        OnboardingPage onboardingPage,
        String message
    ) {

    }

    public record SignupData(
        boolean exists,
        String name,
        String email,
        String message
    ) {

    }
}
