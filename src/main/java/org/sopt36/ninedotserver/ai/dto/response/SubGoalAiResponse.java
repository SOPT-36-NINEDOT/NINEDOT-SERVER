package org.sopt36.ninedotserver.ai.dto.response;

import java.util.List;

public record SubGoalAiResponse(
    List<SubGoalTitleResponse> aiRecommendedList
) {
    
}
