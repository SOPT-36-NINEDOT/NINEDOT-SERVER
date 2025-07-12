package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
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

    private CoreGoalSnapshot findCoreGoalOrThrow(Long coreGoalSnapshotId) {
        return coreGoalSnapshotRepository.findById(coreGoalSnapshotId)
            .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));
    }

}
