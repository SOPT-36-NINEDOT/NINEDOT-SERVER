package org.sopt36.ninedotserver.mandalart.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.model.Cycle;
import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;

public interface SubGoalSnapshotRepositoryPort {

    void deleteBySubGoal_CoreGoal(CoreGoal coreGoal);

    <S extends SubGoalSnapshot> S save(S snapshot);

    Optional<SubGoalSnapshot> findTopBySubGoal_IdOrderByCreatedAtDesc(Long id);

    <S extends SubGoalSnapshot> Iterable<S> saveAll(Iterable<S> latest);

    Optional<SubGoalSnapshot> findById(Long subGoalSnapshotId);

    List<SubGoalSnapshot> findAllByMandalartId(Long mandalartId);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCoreGoalOrderByPosition(
        Long mandalartId, Long id);

    void delete(SubGoalSnapshot subGoalSnapshot);

    List<SubGoalSnapshot> findByCoreGoalSnapshotIdOrderByPosition(Long coreGoalSnapshotId);

    List<SubGoalSnapshot> findAllActiveSubGoalSnapshotOrderByPosition(Long mandalartId,
        LocalDate date);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotByCycleOrderByPosition(Long mandalartId,
        Cycle cycle);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCycleAndCoreGoalOrderByPosition(
        Long mandalartId, Long coreGoalSnapshotId,
        Cycle cycle);

    int countActiveSubGoalSnapshotByCoreGoal(Long coreGoalId);

    List<Integer> findActiveSubGoalPositionsByCoreGoal(Long coreGoalId);

    boolean existsBySubGoal_CoreGoal_Mandalart_IdAndValidFromLessThanEqual(Long mandalartId, LocalDateTime targetDate);
}
