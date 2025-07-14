package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record SubGoalListResponse(
    List<SubGoalDetailResponse> subGoals
) {

    public static SubGoalListResponse of(List<SubGoalDetailResponse> subGoalDetailResponseList) {
        return new SubGoalListResponse(List.copyOf(subGoalDetailResponseList));
    }
}
