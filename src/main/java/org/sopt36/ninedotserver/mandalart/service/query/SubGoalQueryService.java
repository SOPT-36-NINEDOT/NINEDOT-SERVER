package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalSnapshotRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubGoalQueryService {

    private final SubGoalRepository subGoalRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;
    private final CoreGoalSnapshotRepository coreGoalSnapshotRepository;
    private final SubGoalSnapshotRepository subGoalSnapshotRepository;

    public List<SubGoalIdResponse> getSubGoalIds(Long userId, Long coreGoalSnapshotId) {
        CoreGoalSnapshot coreGoalSnapshot = findCoreGoalOrThrow(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);

        List<SubGoalSnapshot> subGoals = subGoalSnapshotRepository
            .findByCoreGoalSnapshotIdOrderByPosition(coreGoalSnapshotId);

        return subGoals.stream()
            .map(SubGoalIdResponse::from)
            .toList();
    }

    public SubGoalListResponse getSubGoalWithFilter(
        Long userId,
        Long mandalartId,
        Long coreGoalSnapshotId,
        Cycle cycle
    ) {
        if (coreGoalSnapshotId == null && cycle == null) {
            return filterNothing(mandalartId);
        }

        if (coreGoalSnapshotId == null) {
            return filterByCycle(mandalartId, cycle);
        }

        if (cycle == null) {
            return filterByCoreGoalSnapshot(userId, mandalartId, coreGoalSnapshotId);
        }

        return filterByCoreGoalSnapshotAndCycle(userId, mandalartId, coreGoalSnapshotId, cycle);
    }

    private CoreGoalSnapshot findCoreGoalOrThrow(Long coreGoalSnapshotId) {
        return coreGoalSnapshotRepository.findById(coreGoalSnapshotId)
            .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));
    }

    private SubGoalListResponse filterNothing(Long mandalartId) {
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findAllActiveSubGoalSnapshot(mandalartId).stream()
                .map(SubGoalDetailResponse::from).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private SubGoalListResponse filterByCycle(Long mandalartId, Cycle cycle) {
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findActiveSubGoalSnapshotByCycle(mandalartId, cycle)
                .stream()
                .map(SubGoalDetailResponse::from).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private SubGoalListResponse filterByCoreGoalSnapshot(Long userId, Long mandalartId,
        Long coreGoalSnapshotId) {
        CoreGoalSnapshot coreGoalSnapshot = findCoreGoalOrThrow(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findActiveSubGoalSnapshotFollowingCoreGoal(mandalartId,
                    coreGoalSnapshotId).stream()
                .map(SubGoalDetailResponse::from).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private SubGoalListResponse filterByCoreGoalSnapshotAndCycle(Long userId, Long mandalartId,
        Long coreGoalSnapshotId, Cycle cycle) {
        CoreGoalSnapshot coreGoalSnapshot = findCoreGoalOrThrow(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findActiveSubGoalSnapshotFollowingCycleAndCoreGoal(
                    mandalartId, coreGoalSnapshotId, cycle).stream()
                .map(SubGoalDetailResponse::from).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }
}
