package org.sopt36.ninedotserver.mandalart.persistence;

import java.time.LocalDate;
import java.util.List;
import org.sopt36.ninedotserver.mandalart.model.Recommendation;
import org.sopt36.ninedotserver.mandalart.port.out.RecommendationRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository
    extends JpaRepository<Recommendation, Long>, RecommendationRepositoryPort {

    List<Recommendation> findAllBySubGoalSnapshot_SubGoal_CoreGoal_Mandalart_IdAndRecommendationDate(
        Long mandalartId,
        LocalDate recommendationDate
    );
}
