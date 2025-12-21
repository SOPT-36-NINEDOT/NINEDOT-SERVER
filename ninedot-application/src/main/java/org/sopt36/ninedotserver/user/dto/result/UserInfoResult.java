package org.sopt36.ninedotserver.user.dto.result;

import org.sopt36.ninedotserver.auth.model.OnboardingPage;
import org.sopt36.ninedotserver.auth.service.login.dto.OnboardingStatus;
import org.sopt36.ninedotserver.user.model.User;

public record UserInfoResult(
        Long id,
        String name,
        String email,
        String profileImageUrl,
        boolean onboardingCompleted,
        OnboardingPage nextPage
) {
    public static UserInfoResult from(
            User user,
            OnboardingStatus onboardingStatus
            ) {
        return new UserInfoResult(
                user.getId(),
                user.nameAsString(),
                user.emailAsString(),
                user.profileImageUrlAsString(),
                onboardingStatus.onboardingCompleted(),
                onboardingStatus.nextPage()
        );
    }
}
