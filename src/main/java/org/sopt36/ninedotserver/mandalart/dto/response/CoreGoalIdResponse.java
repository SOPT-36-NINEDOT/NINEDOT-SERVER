package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;

public record CoreGoalIdResponse(Long id, int position) {

    public static CoreGoalIdResponse from(CoreGoalSnapshot coreGoalSnapshot) {
        return new CoreGoalIdResponse(
            coreGoalSnapshot.getId(),
            coreGoalSnapshot.getCoreGoal().getPosition()
        );
    }
}
