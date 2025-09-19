package org.sopt36.ninedotserver.auth.service.login.dto;

import org.sopt36.ninedotserver.auth.model.OnboardingPage;

public record OnboardingStatus(
    boolean onboardingCompleted,
    OnboardingPage nextPage
) {

}
