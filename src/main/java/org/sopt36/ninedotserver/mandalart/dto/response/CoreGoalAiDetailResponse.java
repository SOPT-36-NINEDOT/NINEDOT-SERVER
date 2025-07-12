package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;

public record CoreGoalAiDetailResponse(
    Long id,
    int position,
    String title
) {

    public static CoreGoalAiDetailResponse of(
        CoreGoal coreGoal,
        CoreGoalSnapshot coreGoalSnapshot
    ) {
        return new CoreGoalAiDetailResponse(
            coreGoal.getId(),
            coreGoal.getPosition(),
            coreGoalSnapshot.getTitle()
        );
    }
}
