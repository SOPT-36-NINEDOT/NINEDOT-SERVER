package org.sopt36.ninedotserver.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubGoalRecommendationRequest(
    @NotBlank
    String title
) {

}
