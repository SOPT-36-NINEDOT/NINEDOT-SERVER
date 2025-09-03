package org.sopt36.ninedotserver.mandalart.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.History;

public interface HistoryRepositoryPort {

    <S extends History> S save(S history);

    Optional<History> findBySubGoalSnapshotIdAndCompletedDate(Long subGoalSnapshotId,
        LocalDate completedDate);

    void delete(History history);

    boolean existsBySubGoalSnapshotIdAndCompletedDate(Long subGoalSnapshotId, LocalDate date);

    List<Long> findCompletedSubGoalIdsByUser(Long userId, LocalDate startDate, LocalDate endDate);

    List<History> findBySubGoalSnapshot_SubGoal_CoreGoal_Mandalart_IdAndCompletedDateBetween(
        Long mandalartId,
        LocalDate startDate, LocalDate today);
}
