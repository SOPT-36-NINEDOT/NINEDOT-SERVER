package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long>, SubGoalRepositoryCustom {

    List<SubGoal> findAllByCoreGoalId(Long coreGoalId);

    int countByCoreGoalId(Long coreGoalId);

    boolean existsByCoreGoalIdAndPosition(Long coreGoalId, int position);

    void deleteByCoreGoal(CoreGoal coreGoal);

    Optional<SubGoal> findByCoreGoalIdAndPosition(Long coreGoalId, int position);
}
