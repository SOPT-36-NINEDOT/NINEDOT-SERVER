package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;

public record CoreGoalCreateResponse(
    Long id
) {

    public static CoreGoalCreateResponse from(CoreGoalSnapshot coreGoalSnapshot) {
        return new CoreGoalCreateResponse(coreGoalSnapshot.getId());
    }
}
