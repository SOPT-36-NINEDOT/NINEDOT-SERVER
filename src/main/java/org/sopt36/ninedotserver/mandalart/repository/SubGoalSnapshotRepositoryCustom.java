package org.sopt36.ninedotserver.mandalart.repository;

import java.time.LocalDate;
import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public interface SubGoalSnapshotRepositoryCustom {

    List<SubGoalSnapshot> findByCoreGoalSnapshotIdOrderByPosition(Long coreGoalId);

    List<SubGoalSnapshot> findAllByMandalartId(Long mandalartId);

    List<SubGoalSnapshot> findAllActiveSubGoalSnapshotOrderByPosition(Long mandalartId,
        LocalDate date);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotByCycleOrderByPosition(Long mandalartId,
        Cycle cycle);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCoreGoalOrderByPosition(
        Long mandalartId,
        Long coreGoalSnapshotId);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCycleAndCoreGoalOrderByPosition(
        Long mandalartId,
        Long coreGoalSnapshotId, Cycle cycle);


    int countActiveSubGoalSnapshotByCoreGoal(Long coreGoalId);

    List<Integer> findActiveSubGoalPositionsByCoreGoal(Long coreGoalId);
}
