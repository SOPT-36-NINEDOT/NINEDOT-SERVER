package org.sopt36.ninedotserver.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SubGoalAiRequest(
    @NotBlank
    String coreGoal,

    List<SubGoalRecommendationRequest> subGoal
) {

}
