package org.sopt36.ninedotserver.mandalart.repository;

import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubGoalSnapshotRepository
    extends JpaRepository<SubGoalSnapshot, Long>, SubGoalSnapshotRepositoryCustom {

    void deleteBySubGoal_CoreGoal(CoreGoal coreGoal);

    Optional<SubGoalSnapshot> findTopBySubGoal_IdOrderByCreatedAtDesc(Long id);
}
