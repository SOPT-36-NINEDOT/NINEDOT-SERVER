package org.sopt36.ninedotserver.mandalart.persistence;

import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.model.SubGoal;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long>, SubGoalRepositoryPort {

    int countByCoreGoalId(Long coreGoalId);

    boolean existsByCoreGoalIdAndPosition(Long coreGoalId, int position);

    void deleteByCoreGoal(CoreGoal coreGoal);

    Optional<SubGoal> findByCoreGoalIdAndPosition(Long coreGoalId, int position);
}
