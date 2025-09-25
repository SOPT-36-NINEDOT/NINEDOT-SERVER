package org.sopt36.ninedotserver.ai.dto.result;

import java.util.List;

public record AiSubGoalResult(List<SubGoalRecommendationResult> aiRecommendedList) {

    public record SubGoalRecommendationResult(
            String title,
            String cycle
    ) {

    }

}
