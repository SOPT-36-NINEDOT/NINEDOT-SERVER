package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalSnapshotRepository;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
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
    private final HistoryRepository historyRepository;

    public List<SubGoalIdResponse> getSubGoalIds(Long userId, Long coreGoalSnapshotId) {
        CoreGoalSnapshot coreGoalSnapshot = findCoreGoalOrThrow(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);

        List<SubGoalSnapshot> subGoals = subGoalSnapshotRepository
            .findByCoreGoalSnapshotIdOrderByPosition(
                coreGoalSnapshotId);

        return subGoals.stream()
            .map(SubGoalIdResponse::from)
            .toList();
    }

    public SubGoalListResponse getSubGoalWithFilter(
        Long userId,
        Long mandalartId,
        Long coreGoalSnapshotId,
        Cycle cycle,
        LocalDate date
    ) {
        if (coreGoalSnapshotId == null && cycle == null) {
            return filterNothing(userId, mandalartId, date);
        }

        if (coreGoalSnapshotId == null) {
            return filterByCycle(userId, mandalartId, cycle, date);
        }

        if (cycle == null) {
            return filterByCoreGoalSnapshot(userId, mandalartId, coreGoalSnapshotId, date);
        }

        return filterByCoreGoalSnapshotAndCycle(userId, mandalartId, coreGoalSnapshotId, cycle,
            date);
    }

    private CoreGoalSnapshot findCoreGoalOrThrow(Long coreGoalSnapshotId) {
        return coreGoalSnapshotRepository.findById(coreGoalSnapshotId)
            .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));
    }

    private SubGoalListResponse filterNothing(Long userId, Long mandalartId, LocalDate date) {
        verifyMandalartByUser(userId, mandalartId);
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findAllActiveSubGoalSnapshotOrderByPosition(mandalartId, date)
                .stream()
                .map(subGoalSnapshot -> {
                        boolean isCompleted = historyRepository
                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                        return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                    }
                ).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private SubGoalListResponse filterByCycle(Long userId, Long mandalartId, Cycle cycle,
        LocalDate date) {
        verifyMandalartByUser(userId, mandalartId);
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findActiveSubGoalSnapshotByCycleOrderByPosition(mandalartId,
                    cycle)
                .stream()
                .map(subGoalSnapshot -> {
                        boolean isCompleted = historyRepository
                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                        return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                    }
                ).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private SubGoalListResponse filterByCoreGoalSnapshot(Long userId, Long mandalartId,
        Long coreGoalSnapshotId, LocalDate date) {
        verifyMandalartByUser(userId, mandalartId);
        verifyCoreGoalByUser(userId, coreGoalSnapshotId);
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findActiveSubGoalSnapshotFollowingCoreGoalOrderByPosition(
                    mandalartId,
                    coreGoalSnapshotId).stream()
                .map(subGoalSnapshot -> {
                        boolean isCompleted = historyRepository
                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                        return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                    }
                ).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private SubGoalListResponse filterByCoreGoalSnapshotAndCycle(Long userId, Long mandalartId,
        Long coreGoalSnapshotId, Cycle cycle, LocalDate date) {
        verifyMandalartByUser(userId, mandalartId);
        verifyCoreGoalByUser(userId, coreGoalSnapshotId);
        return SubGoalListResponse.of(
            subGoalSnapshotRepository.findActiveSubGoalSnapshotFollowingCycleAndCoreGoalOrderByPosition(
                    mandalartId, coreGoalSnapshotId, cycle).stream()
                .map(subGoalSnapshot -> {
                        boolean isCompleted = historyRepository
                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                        return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                    }
                ).filter(subGoal -> !subGoal.title().isEmpty())
                .toList());
    }

    private void verifyMandalartByUser(Long userId, Long mandalartId) {
        Mandalart mandalart = mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
        mandalart.ensureOwnedBy(userId);
    }

    private void verifyCoreGoalByUser(Long userId, Long coreGoalSnapshotId) {
        CoreGoalSnapshot coreGoalSnapshot = findCoreGoalOrThrow(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);
    }
}
