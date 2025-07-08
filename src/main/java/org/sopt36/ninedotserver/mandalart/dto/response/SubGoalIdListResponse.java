package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record SubGoalIdListResponse(
    List<SubGoalIdResponse> subGoalIds
) {

    public static SubGoalIdListResponse from(List<SubGoalIdResponse> list) {
        return new SubGoalIdListResponse(list);
    }

}
