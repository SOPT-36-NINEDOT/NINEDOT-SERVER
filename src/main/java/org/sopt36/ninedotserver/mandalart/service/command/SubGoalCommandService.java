package org.sopt36.ninedotserver.mandalart.service.command;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_COMPLETED;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_CONFLICT;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_NOT_FOUND;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.service.AiRecommendationService;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.SubGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalSnapshotRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubGoalCommandService {

    private static final int MAX_SUB_GOALS = 8;

    private final SubGoalRepository subGoalRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final AiRecommendationService aiRecommendationService;
    private final CoreGoalSnapshotRepository coreGoalSnapshotRepository;
    private final SubGoalSnapshotRepository subGoalSnapshotRepository;

    @Transactional
    public SubGoalCreateResponse createSubGoal(
        Long userId,
        Long coreGoalSnapshotId,
        SubGoalCreateRequest request
    ) {
        CoreGoalSnapshot coreGoalSnapshot = getExistingCoreGoal(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);
        // TODO validateCreate 로직 변경 생각해보기

        SubGoal subGoal = SubGoal.create(
            coreGoalSnapshot.getCoreGoal(),
            request.position()
        );
        subGoalRepository.save(subGoal);

        SubGoalSnapshot subGoalSnapshot = SubGoalSnapshot.create(
            subGoal,
            request.title(),
            request.cycle(),
            LocalDateTime.now(),
            null
        );
        subGoalSnapshotRepository.save(subGoalSnapshot);

        coreGoalSnapshot.getCoreGoal().getUser().completeOnboarding();

        return SubGoalCreateResponse.from(subGoalSnapshot);
    }

    @Transactional
    public void updateSubGoal(Long userId, Long subGoalId, SubGoalUpdateRequest request) {
        SubGoal subGoal = getExistingSubGoal(subGoalId);
        subGoal.verifyUser(userId);
    }

    @Transactional
    public void deleteSubGoal(Long userId, Long subGoalId) {
        SubGoal subGoal = getExistingSubGoal(subGoalId);
        subGoal.verifyUser(userId);
        subGoalRepository.delete(subGoal);
    }

    private void validateCreate(CoreGoalSnapshot coreGoalSnapshot, Long userId,
        SubGoalCreateRequest request) {
        validateSubGoalLimitNotExceeded(coreGoalSnapshot.getId());
        validateAlreadyExists(coreGoalSnapshot.getId(), request.position());
    }

    private CoreGoalSnapshot getExistingCoreGoal(Long coreGoalId) {
        return coreGoalSnapshotRepository.findById(coreGoalId)
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


}
