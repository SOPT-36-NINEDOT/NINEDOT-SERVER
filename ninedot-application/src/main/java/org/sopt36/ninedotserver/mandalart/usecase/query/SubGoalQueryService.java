package org.sopt36.ninedotserver.mandalart.usecase.query;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.model.Cycle;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalSnapshotRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.HistoryRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalSnapshotRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubGoalQueryService {

    private final MandalartRepositoryPort mandalartRepository;
    private final CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository;
    private final SubGoalSnapshotRepositoryPort subGoalSnapshotRepository;
    private final HistoryRepositoryPort historyRepository;

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
            Cycle cycle,
            LocalDate date
    ) {

        boolean isYesterdayExist = hasYesterdayData(mandalartId, date.minusDays(1));

        if (coreGoalSnapshotId == null && cycle == null) {
            return filterNothing(userId, mandalartId, date, isYesterdayExist);
        }

        if (coreGoalSnapshotId == null) {
            return filterByCycle(userId, mandalartId, cycle, date, isYesterdayExist);
        }

        if (cycle == null) {
            return filterByCoreGoalSnapshot(userId, mandalartId, coreGoalSnapshotId, date, isYesterdayExist);
        }

        return filterByCoreGoalSnapshotAndCycle(userId, mandalartId, coreGoalSnapshotId, cycle,
                date, isYesterdayExist);
    }

    private boolean hasYesterdayData(Long mandalartId, LocalDate yesterday) {
        LocalDateTime endOfDay = yesterday.atTime(23, 59, 59);
        return subGoalSnapshotRepository.existsBySubGoal_CoreGoal_Mandalart_IdAndValidFromLessThanEqual(mandalartId, endOfDay);
    }

    private CoreGoalSnapshot findCoreGoalOrThrow(Long coreGoalSnapshotId) {
        return coreGoalSnapshotRepository.findById(coreGoalSnapshotId)
                .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));
    }

    private SubGoalListResponse filterNothing(Long userId, Long mandalartId, LocalDate date, boolean isYesterdayExist) {
        verifyMandalartByUser(userId, mandalartId);
        return SubGoalListResponse.of(
                isYesterdayExist,
                subGoalSnapshotRepository.findAllActiveSubGoalSnapshotOrderByPosition(mandalartId, date)
                        .stream()
                        .map(subGoalSnapshot -> {
                                    boolean isCompleted = historyRepository
                                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                                    return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                                }
                        ).filter(subGoal -> !subGoal.title().isEmpty())
                        .toList()
        );
    }

    private SubGoalListResponse filterByCycle(Long userId, Long mandalartId, Cycle cycle,
                                              LocalDate date, boolean isYesterdayExist) {
        verifyMandalartByUser(userId, mandalartId);
        return SubGoalListResponse.of(
                isYesterdayExist,
                subGoalSnapshotRepository.findActiveSubGoalSnapshotByCycleOrderByPosition(mandalartId,
                                cycle)
                        .stream()
                        .map(subGoalSnapshot -> {
                                    boolean isCompleted = historyRepository
                                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                                    return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                                }
                        ).filter(subGoal -> !subGoal.title().isEmpty())
                        .toList()
        );
    }

    private SubGoalListResponse filterByCoreGoalSnapshot(Long userId, Long mandalartId,
                                                         Long coreGoalSnapshotId, LocalDate date, boolean isYesterdayExist) {
        verifyMandalartByUser(userId, mandalartId);
        verifyCoreGoalByUser(userId, coreGoalSnapshotId);
        return SubGoalListResponse.of(
                isYesterdayExist,
                subGoalSnapshotRepository.findActiveSubGoalSnapshotFollowingCoreGoalOrderByPosition(
                                mandalartId,
                                coreGoalSnapshotId).stream()
                        .map(subGoalSnapshot -> {
                                    boolean isCompleted = historyRepository
                                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                                    return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                                }
                        ).filter(subGoal -> !subGoal.title().isEmpty())
                        .toList()
        );
    }

    private SubGoalListResponse filterByCoreGoalSnapshotAndCycle(Long userId, Long mandalartId,
                                                                 Long coreGoalSnapshotId, Cycle cycle, LocalDate date, boolean isYesterdayExist) {
        verifyMandalartByUser(userId, mandalartId);
        verifyCoreGoalByUser(userId, coreGoalSnapshotId);
        return SubGoalListResponse.of(
                isYesterdayExist,
                subGoalSnapshotRepository.findActiveSubGoalSnapshotFollowingCycleAndCoreGoalOrderByPosition(
                                mandalartId, coreGoalSnapshotId, cycle).stream()
                        .map(subGoalSnapshot -> {
                                    boolean isCompleted = historyRepository
                                            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshot.getId(), date);
                                    return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
                                }
                        ).filter(subGoal -> !subGoal.title().isEmpty())
                        .toList()
        );
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
