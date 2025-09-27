package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record RecommendationListResponse(
        List<SubGoalDetailResponse> subGoals
) {
    public static RecommendationListResponse of(List<SubGoalDetailResponse> subGoals) {
        return new RecommendationListResponse(List.copyOf(subGoals));
    }
}
