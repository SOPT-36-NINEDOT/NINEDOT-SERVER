package org.sopt36.ninedotserver.mandalart.repository;

import java.time.LocalDate;
import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findAllBySubGoalSnapshot_SubGoal_CoreGoal_Mandalart_IdAndRecommendationDate(
        Long mandalartId,
        LocalDate recommendationDate
    );
}
