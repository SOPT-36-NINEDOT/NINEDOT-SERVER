package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record CoreGoalsResponse(List<CoreGoalDetailResponse> coreGoals) {

    public static CoreGoalsResponse of(List<CoreGoalDetailResponse> coreGoals) {
        return new CoreGoalsResponse(List.copyOf(coreGoals));
    }
}
