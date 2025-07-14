package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public interface SubGoalSnapshotRepositoryCustom {

    List<SubGoalSnapshot> findByCoreGoalSnapshotIdOrderByPosition(Long coreGoalId);

    List<SubGoalSnapshot> findAllByMandalartId(Long mandalartId);

    List<SubGoalSnapshot> findAllActiveSubGoalSnapshot(Long mandalartId);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotByCycle(Long mandalartId, Cycle cycle);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCoreGoal(Long mandalartId,
        Long coreGoalSnapshotId);

    List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCycleAndCoreGoal(Long mandalartId,
        Long coreGoalSnapshotId, Cycle cycle);
}
