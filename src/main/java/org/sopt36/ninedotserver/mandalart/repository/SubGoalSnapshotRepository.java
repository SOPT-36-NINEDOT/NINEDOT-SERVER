package org.sopt36.ninedotserver.mandalart.repository;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubGoalSnapshotRepository
    extends JpaRepository<SubGoalSnapshot, Long>, SubGoalSnapshotRepositoryCustom {

    void deleteBySubGoal_CoreGoal(CoreGoal coreGoal);

}
