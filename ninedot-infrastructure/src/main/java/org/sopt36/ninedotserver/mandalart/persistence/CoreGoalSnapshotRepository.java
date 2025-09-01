package org.sopt36.ninedotserver.mandalart.persistence;

import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.persistence.querydsl.CoreGoalSnapshotRepositoryCustom;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalSnapshotRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoreGoalSnapshotRepository
    extends JpaRepository<CoreGoalSnapshot, Long>, CoreGoalSnapshotRepositoryPort,
    CoreGoalSnapshotRepositoryCustom {

    Optional<CoreGoalSnapshot> findTopByCoreGoal_IdOrderByCreatedAtDesc(Long coreGoalId);
}
