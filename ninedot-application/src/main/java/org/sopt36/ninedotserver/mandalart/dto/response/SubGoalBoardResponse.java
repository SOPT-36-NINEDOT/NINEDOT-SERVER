package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;

public record SubGoalBoardResponse(
    Long id,
    String title,
    int position
) {

    public static SubGoalBoardResponse from(SubGoalSnapshot subGoalSnapshot) {
        return new SubGoalBoardResponse(
            subGoalSnapshot.getId(),
            subGoalSnapshot.getTitle(),
            subGoalSnapshot.getSubGoal().getPosition()
        );
    }
}
