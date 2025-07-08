package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.SubGoal;

public record SubGoalIdResponse(
    Long id,
    int position
) {

    public static SubGoalIdResponse from(SubGoal subGoal) {
        return new SubGoalIdResponse(subGoal.getId(), subGoal.getPosition());
    }

}
