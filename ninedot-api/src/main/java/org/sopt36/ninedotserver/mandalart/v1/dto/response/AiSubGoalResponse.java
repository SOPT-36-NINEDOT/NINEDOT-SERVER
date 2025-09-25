package org.sopt36.ninedotserver.mandalart.v1.dto.response;

import java.util.List;

public record AiSubGoalResponse(List<AiSubGoalRecommendationResponse> aiRecommendedList) {

    public record AiSubGoalRecommendationResponse(
            String title,
            String cycle
    ) {
    }
}
