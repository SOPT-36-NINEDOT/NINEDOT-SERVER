package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record SubGoalListResponse(
        boolean isYesterdayExist,
        List<SubGoalDetailResponse> subGoals
) {

    public static SubGoalListResponse of(boolean isYesterdayExist, List<SubGoalDetailResponse> subGoalDetailResponseList) {
        return new SubGoalListResponse(isYesterdayExist, List.copyOf(subGoalDetailResponseList));
    }
}
