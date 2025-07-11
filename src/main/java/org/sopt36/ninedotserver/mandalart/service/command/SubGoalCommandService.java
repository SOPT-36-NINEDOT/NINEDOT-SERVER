package org.sopt36.ninedotserver.mandalart.service.command;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_COMPLETED;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_CONFLICT;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.service.AiRecommendationService;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoal;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubGoalCommandService {

    private static final int MAX_SUB_GOALS = 8;

    private final SubGoalRepository subGoalRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final AiRecommendationService aiRecommendationService;

    @Transactional
    public SubGoalCreateResponse createSubGoal(Long userId, Long coreGoalId,
        SubGoalCreateRequest request) {

        CoreGoal coreGoal = getExistingCoreGoal(coreGoalId);
        validateCreate(coreGoal, userId, request);

        SubGoal subGoal = SubGoal.create(
            coreGoal,
            request.title(),
            request.position(),
            request.cycle()
        );

        subGoalRepository.save(subGoal);

        User user = getUserFrom(coreGoal);

        if (!user.getOnboardingCompleted()) {
            user.completeOnboarding();
        }

        return SubGoalCreateResponse.from(subGoal);
    }

    @Transactional
    public void updateSubGoal(Long userId, Long subGoalId, SubGoalUpdateRequest request) {
        SubGoal subGoal = getExistingSubGoal(subGoalId);
        subGoal.verifyUser(userId);
        subGoal.update(request.title(), request.cycle());
    }

    @Transactional
    public void deleteSubGoal(Long userId, Long subGoalId) {
        SubGoal subGoal = getExistingSubGoal(subGoalId);
        subGoal.verifyUser(userId);
        subGoalRepository.delete(subGoal);
    }

    private void validateCreate(CoreGoal coreGoal, Long userId, SubGoalCreateRequest request) {
        coreGoal.verifyUser(userId);
        validateSubGoalLimitNotExceeded(coreGoal.getId());
        validateAlreadyExists(coreGoal.getId(), request.position());
    }

    private CoreGoal getExistingCoreGoal(Long coreGoalId) {
        return coreGoalRepository.findById(coreGoalId)
            .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));
    }

    private void validateSubGoalLimitNotExceeded(Long coreGoalId) {
        if (subGoalRepository.countByCoreGoalId(coreGoalId) >= MAX_SUB_GOALS) {
            throw new SubGoalException(SUB_GOAL_COMPLETED);
        }
    }

    private void validateAlreadyExists(Long coreGoalId, int position) {
        if (subGoalRepository.existsByCoreGoalIdAndPosition(coreGoalId, position)) {
            throw new SubGoalException(SUB_GOAL_CONFLICT);
        }
    }

    private SubGoal getExistingSubGoal(Long subGoalId) {
        return subGoalRepository.findById(subGoalId)
            .orElseThrow(() -> new SubGoalException(SUB_GOAL_NOT_FOUND));
    }

    private User getUserFrom(CoreGoal coreGoal) {
        return coreGoal.getMandalart().getUser();
    }


}
