package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record CoreGoalAiListResponse(
    List<CoreGoalAiDetailResponse> coreGoals
) {

    public static CoreGoalAiListResponse of(List<CoreGoalAiDetailResponse> coreGoals) {
        return new CoreGoalAiListResponse(List.copyOf(coreGoals));
    }
}
