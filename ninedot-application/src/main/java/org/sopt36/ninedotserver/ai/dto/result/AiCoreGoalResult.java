package org.sopt36.ninedotserver.ai.dto.result;

import java.util.List;

public record AiCoreGoalResult(List<AiCoreGoalRecommendationResult> aiRecommendedList) {

    public record AiCoreGoalRecommendationResult(String title) {

    }

}
