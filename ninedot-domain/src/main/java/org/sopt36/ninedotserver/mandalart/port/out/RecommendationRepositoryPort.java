package org.sopt36.ninedotserver.mandalart.port.out;

import java.time.LocalDate;
import java.util.List;
import org.sopt36.ninedotserver.mandalart.model.Recommendation;

public interface RecommendationRepositoryPort {

    <S extends Recommendation> S save(S recommendation);

    List<Recommendation> findAllBySubGoalSnapshot_SubGoal_CoreGoal_Mandalart_IdAndRecommendationDate(
        Long mandalartId,
        LocalDate date);
}
