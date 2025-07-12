package org.sopt36.ninedotserver.mandalart.service.command;

import static org.sopt36.ninedotserver.mandalart.exception.HistoryErrorCode.HISTORY_ALREADY_COMPLETED;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_NOT_FOUND;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.History;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.exception.HistoryException;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HistoryCommandService {

    private final HistoryRepository historyRepository;
    private final SubGoalSnapshotRepository subGoalSnapshotRepository;

    @Transactional
    public Long createHistory(Long userId, Long subGoalSnapshotId) {
        SubGoalSnapshot subGoalSnapshot = getValidSubGoalSnapshot(subGoalSnapshotId);
        subGoalSnapshot.verifySubGoalUser(userId);
        validateCanCompleteSubGoal(subGoalSnapshotId);

        History history = History.create(subGoalSnapshot, LocalDate.now());
        historyRepository.save(history);

        return history.getId();
    }

    private SubGoalSnapshot getValidSubGoalSnapshot(Long subGoalSnapshotId) {
        return subGoalSnapshotRepository.findById(subGoalSnapshotId)
            .orElseThrow(() -> new SubGoalException(SUB_GOAL_NOT_FOUND));
    }

    private void validateCanCompleteSubGoal(Long subGoalSnapshotId) {
        if (historyRepository
            .existsBySubGoalSnapshotIdAndCompletedDate(subGoalSnapshotId, LocalDate.now())
        ) {
            throw new HistoryException(HISTORY_ALREADY_COMPLETED);
        }
    }
}
