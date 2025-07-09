package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.SubGoal;

public record SubGoalCreateResponse(
    Long id
) {

    public static SubGoalCreateResponse from(SubGoal subgoal) {
        return new SubGoalCreateResponse(subgoal.getId());
    }

}
