package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;

public record CoreGoalIdResponse(Long id, int position) {

    public static CoreGoalIdResponse from(CoreGoal coreGoal) {
        return new CoreGoalIdResponse(coreGoal.getId(), coreGoal.getPosition());
    }
}
