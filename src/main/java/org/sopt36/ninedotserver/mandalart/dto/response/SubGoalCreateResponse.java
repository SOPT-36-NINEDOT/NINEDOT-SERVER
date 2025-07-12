package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public record SubGoalCreateResponse(
    Long id
) {

    public static SubGoalCreateResponse from(SubGoalSnapshot subGoalSnapshot) {
        return new SubGoalCreateResponse(subGoalSnapshot.getId());
    }
}
