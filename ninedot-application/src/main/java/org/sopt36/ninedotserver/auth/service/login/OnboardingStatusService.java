package org.sopt36.ninedotserver.auth.service.login;

import static org.sopt36.ninedotserver.auth.model.OnboardingPage.CORE_GOAL;
import static org.sopt36.ninedotserver.auth.model.OnboardingPage.MANDALART;
import static org.sopt36.ninedotserver.auth.model.OnboardingPage.ONBOARDING_COMPLETED;
import static org.sopt36.ninedotserver.auth.model.OnboardingPage.SUB_GOAL;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.service.login.dto.OnboardingStatus;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OnboardingStatusService {

    private final UserRepositoryPort userRepositoryPort;
    private final CoreGoalRepositoryPort coreGoalRepositoryPort;
    private final MandalartRepositoryPort mandalartRepositoryPort;

    public OnboardingStatus determineOnboardingStatus(Long userId) {
        boolean onboardingCompleted = userRepositoryPort.findById(userId)
            .map(user -> Boolean.TRUE.equals(user.getOnboardingCompleted()))
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        if (onboardingCompleted) {
            return new OnboardingStatus(true, ONBOARDING_COMPLETED);
        }

        boolean hasCoreGoals = coreGoalRepositoryPort.existsByUserId(userId);
        if (hasCoreGoals) {
            return new OnboardingStatus(false, SUB_GOAL);
        }

        boolean hasMandalart = mandalartRepositoryPort.existsByUserId(userId);
        if (hasMandalart) {
            return new OnboardingStatus(false, CORE_GOAL);
        }

        return new OnboardingStatus(false, MANDALART);
    }

}
