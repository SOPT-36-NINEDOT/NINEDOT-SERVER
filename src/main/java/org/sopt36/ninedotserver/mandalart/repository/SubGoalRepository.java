package org.sopt36.ninedotserver.mandalart.repository;

import org.sopt36.ninedotserver.mandalart.domain.SubGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long>, SubGoalRepositoryCustom {

}
