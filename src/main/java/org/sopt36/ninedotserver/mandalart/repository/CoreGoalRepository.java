package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreGoalRepository
    extends JpaRepository<CoreGoal, Long>, CoreGoalRepositoryCustom {

    int countCoreGoalByMandalartId(Long mandalartId);

    boolean existsByMandalartIdAndPosition(Long mandalartId, int position);

    List<CoreGoal> findByMandalartId(Long mandalartId);

    boolean existsByPosition(int position);

    Optional<CoreGoal> findByMandalartIdAndPosition(Long mandalartId, int coreGoalPosition);
}
