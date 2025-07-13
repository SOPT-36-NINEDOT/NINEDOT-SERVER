package org.sopt36.ninedotserver.ai.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.Cycle;

public record SubGoalRecommendationResponse(
    String title,
    Cycle cycle
) {

}
