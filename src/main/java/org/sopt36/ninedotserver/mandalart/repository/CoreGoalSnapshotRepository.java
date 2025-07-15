package org.sopt36.ninedotserver.mandalart.repository;

import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoreGoalSnapshotRepository
    extends JpaRepository<CoreGoalSnapshot, Long>, CoreGoalSnapshotRepositoryCustom {

    Optional<CoreGoalSnapshot> findTopByCoreGoal_IdOrderByCreatedAtDesc(Long coreGoalId);
}
