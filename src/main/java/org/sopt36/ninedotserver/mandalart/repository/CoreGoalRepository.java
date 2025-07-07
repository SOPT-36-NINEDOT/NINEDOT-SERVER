package org.sopt36.ninedotserver.mandalart.repository;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreGoalRepository
    extends JpaRepository<CoreGoal, Long>, CoreGoalRepositoryCustom {

}
