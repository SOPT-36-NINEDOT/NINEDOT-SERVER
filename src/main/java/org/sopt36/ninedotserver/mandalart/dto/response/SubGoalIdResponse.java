package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public record SubGoalIdResponse(
    Long id,
    int position
) {

    public static SubGoalIdResponse from(SubGoalSnapshot subGoalSnapshot) {
        return new SubGoalIdResponse(
            subGoalSnapshot.getId(),
            subGoalSnapshot.getSubGoal().getPosition()
        );
    }
}
