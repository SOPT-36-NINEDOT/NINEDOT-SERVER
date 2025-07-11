package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;

public record CoreGoalDetailResponse(
    Long id,
    int position,
    boolean aiGeneratable
) {

    public static CoreGoalDetailResponse from(CoreGoalSnapshot coreGoalSnapshot) {
        return new CoreGoalDetailResponse(
            coreGoalSnapshot.getId(),
            coreGoalSnapshot.getCoreGoal().getPosition(),
            coreGoalSnapshot.getCoreGoal().isAiGeneratable()
        );
    }
}
