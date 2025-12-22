package org.sopt36.ninedotserver.user.v1.dto.response;

import org.sopt36.ninedotserver.auth.model.OnboardingPage;

public record UserInfoResponse(
        Long id,
        String name,
        String email,
        String profileImageUrl,
        boolean onboardingCompleted,
        OnboardingPage nextPage
) {
}
