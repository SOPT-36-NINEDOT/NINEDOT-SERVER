package org.sopt36.ninedotserver.mandalart.persistence;

import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.persistence.querydsl.CoreGoalRepositoryCustom;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreGoalRepository
    extends JpaRepository<CoreGoal, Long>, CoreGoalRepositoryPort, CoreGoalRepositoryCustom {

    int countCoreGoalByMandalartId(Long mandalartId);

    boolean existsByMandalartIdAndPosition(Long mandalartId, int position);

    List<CoreGoal> findByMandalartId(Long mandalartId);

    Optional<CoreGoal> findByMandalartIdAndPosition(Long mandalartId, int coreGoalPosition);
}
