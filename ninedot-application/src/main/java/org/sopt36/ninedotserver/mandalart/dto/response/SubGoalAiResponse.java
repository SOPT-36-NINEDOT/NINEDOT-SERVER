package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;

public record SubGoalAiResponse(
    Long id,
    String title,
    int position,
    String cycle
) {

    public static SubGoalAiResponse from(SubGoalSnapshot subGoalSnapshot) {
        return new SubGoalAiResponse(
            subGoalSnapshot.getId(),
            subGoalSnapshot.getTitle(),
            subGoalSnapshot.getSubGoal().getPosition(),
            subGoalSnapshot.getCycle().toString()
        );
    }
}
