package org.sopt36.ninedotserver.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import org.sopt36.ninedotserver.auth.model.OnboardingPage;

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
        String socialProvider,
        String socialToken,
        boolean exists,
        String name,
        String email,
        String profileImageUrl,
        String message
    ) {

    }
}
