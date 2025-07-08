package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;

public record CoreGoalCreateResponse(
    Long id
) {

    public static CoreGoalCreateResponse from(CoreGoal coreGoal) {
        return new CoreGoalCreateResponse(coreGoal.getId());
    }
}
