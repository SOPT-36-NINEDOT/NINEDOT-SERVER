package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;

public record CoreGoalBoardResponse(
    Long id,
    String title,
    int position,
    List<SubGoalBoardResponse> subGoals
) {

    public static CoreGoalBoardResponse of(
        CoreGoalSnapshot coreGoalSnapshot,
        List<SubGoalBoardResponse> subGoals
    ) {
        return new CoreGoalBoardResponse(
            coreGoalSnapshot.getId(),
            coreGoalSnapshot.getTitle(),
            coreGoalSnapshot.getCoreGoal().getPosition(),
            List.copyOf(subGoals)
        );
    }
}
