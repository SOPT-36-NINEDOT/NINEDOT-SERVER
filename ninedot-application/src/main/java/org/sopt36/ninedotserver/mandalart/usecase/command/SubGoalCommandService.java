package org.sopt36.ninedotserver.mandalart.usecase.command;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_COMPLETED;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_CONFLICT;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_LIMITED;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.model.SubGoal;
import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalAiListCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalAiListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalSnapshotRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalSnapshotRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubGoalCommandService {

    private static final int MAX_SUB_GOALS = 8;

    private final SubGoalRepositoryPort subGoalRepository;
    private final CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository;
    private final SubGoalSnapshotRepositoryPort subGoalSnapshotRepository;

    @Transactional
    public SubGoalCreateResponse createSubGoal(
        Long userId,
        Long coreGoalSnapshotId,
        SubGoalCreateRequest request
    ) {
        CoreGoalSnapshot coreGoalSnapshot = getExistingCoreGoal(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);
        // TODO validateCreate 로직 변경 생각해보기
        validateCreate(coreGoalSnapshot, userId, request);

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
    public void updateSubGoal(Long userId, Long subGoalSnapshotId, SubGoalUpdateRequest request) {
        SubGoalSnapshot subGoalSnapshot = getExistingSubGoal(subGoalSnapshotId);
        subGoalSnapshot.verifySubGoalUser(userId);
        subGoalSnapshot.update(request.title(), request.cycle());
    }

    @Transactional
    public void deleteSubGoal(Long userId, Long subGoalId) {
        SubGoalSnapshot subGoalSnapshot = getExistingSubGoal(subGoalId);
        subGoalSnapshot.verifySubGoalUser(userId);
        SubGoal subGoal = subGoalSnapshot.getSubGoal();
        subGoalSnapshotRepository.delete(subGoalSnapshot);
        subGoalRepository.delete(subGoal);
    }

    @Transactional
    public SubGoalAiListResponse createAiSubGoals(
        Long userId,
        Long coreGoalSnapshotId,
        SubGoalAiListCreateRequest createRequest
    ) {
        CoreGoalSnapshot coreGoalSnapshot = getExistingCoreGoal(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);
        validateSubGoalCounts(coreGoalSnapshot.getCoreGoal().getId(), createRequest);

        List<Integer> freePositions = findFreePositions(
            coreGoalSnapshot.getCoreGoal().getId(),
            createRequest.goals().size()
        );
        List<SubGoal> subGoals = buildSubGoals(coreGoalSnapshot.getCoreGoal(), freePositions);
        List<SubGoalSnapshot> snapshots = buildSnapshots(subGoals, createRequest);

        subGoalRepository.saveAll(subGoals);
        subGoalSnapshotRepository.saveAll(snapshots);

        return SubGoalAiListResponse.of(snapshots);
    }

    private void validateCreate(
        CoreGoalSnapshot coreGoalSnapshot,
        Long userId,
        SubGoalCreateRequest request
    ) {
        coreGoalSnapshot.verifyCoreGoalUser(userId);
        validateSubGoalLimitNotExceeded(coreGoalSnapshot.getId());
        validateAlreadyExists(coreGoalSnapshot.getId(), request.position());
    }

    private CoreGoalSnapshot getExistingCoreGoal(Long coreGoalSnapshotId) {
        return coreGoalSnapshotRepository.findById(coreGoalSnapshotId)
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

    private SubGoalSnapshot getExistingSubGoal(Long subGoalSnapshotId) {
        return subGoalSnapshotRepository.findById(subGoalSnapshotId)
            .orElseThrow(() -> new SubGoalException(SUB_GOAL_NOT_FOUND));
    }

    private void validateSubGoalCounts(
        Long coreGoalId,
        SubGoalAiListCreateRequest createRequest
    ) {
        int existingSubGoals = subGoalSnapshotRepository
            .countActiveSubGoalSnapshotByCoreGoal(coreGoalId);
        int requestSubGoals = createRequest.goals().size();
        if (existingSubGoals + requestSubGoals > MAX_SUB_GOALS) {
            throw new SubGoalException(SUB_GOAL_LIMITED);
        }
    }

    private List<Integer> findFreePositions(Long coreGoalId, int requiredCount) {
        List<Integer> occupied = subGoalSnapshotRepository.findActiveSubGoalPositionsByCoreGoal(
            coreGoalId);

        return IntStream.rangeClosed(1, MAX_SUB_GOALS)
            .filter(pos -> !occupied.contains(pos))
            .limit(requiredCount)
            .boxed()
            .toList();
    }

    private List<SubGoal> buildSubGoals(CoreGoal coreGoal, List<Integer> positions) {
        return positions.stream()
            .map(position -> SubGoal.create(coreGoal, position))
            .toList();
    }

    private List<SubGoalSnapshot> buildSnapshots(
        List<SubGoal> subGoals,
        SubGoalAiListCreateRequest createRequest
    ) {
        LocalDateTime now = LocalDateTime.now();

        return IntStream.range(0, subGoals.size())
            .mapToObj(i -> SubGoalSnapshot.create(
                subGoals.get(i),
                createRequest.goals().get(i).title(),
                createRequest.goals().get(i).cycle(),
                now,
                null))
            .toList();
    }

}
