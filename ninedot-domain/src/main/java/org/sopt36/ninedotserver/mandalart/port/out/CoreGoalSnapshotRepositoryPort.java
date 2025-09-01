package org.sopt36.ninedotserver.mandalart.port.out;

import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;

public interface CoreGoalSnapshotRepositoryPort {

    List<String> findActiveCoreGoalTitleByMandalartId(Long mandalartId);

    Optional<CoreGoalSnapshot> findById(Long coreGoalId);

    <S extends CoreGoalSnapshot> S save(CoreGoalSnapshot coreGoalSnapshot);

    void delete(CoreGoalSnapshot coreGoalSnapshot);

    <S extends CoreGoalSnapshot> Iterable<S> saveAll(Iterable<S> snapshots);

    Optional<CoreGoalSnapshot> findTopByCoreGoal_IdOrderByCreatedAtDesc(Long id);

    List<CoreGoalSnapshot> findByMandalartIdOrderByPosition(Long mandalartId);
}
