package org.sopt36.ninedotserver.mandalart.v1.dto.response;

import java.util.List;

public record AiCoreGoalResponse(List<AiCoreGoalRecommendationResponse> aiRecommendedList) {

    public record AiCoreGoalRecommendationResponse(
            String title
    ) {

    }

}
