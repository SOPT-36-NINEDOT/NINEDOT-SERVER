package org.sopt36.ninedotserver.mandalart.persistence;

import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.persistence.querydsl.SubGoalSnapshotRepositoryCustom;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalSnapshotRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubGoalSnapshotRepository
    extends JpaRepository<SubGoalSnapshot, Long>, SubGoalSnapshotRepositoryPort,
    SubGoalSnapshotRepositoryCustom {

    void deleteBySubGoal_CoreGoal(CoreGoal coreGoal);

    Optional<SubGoalSnapshot> findTopBySubGoal_IdOrderByCreatedAtDesc(Long id);
}
