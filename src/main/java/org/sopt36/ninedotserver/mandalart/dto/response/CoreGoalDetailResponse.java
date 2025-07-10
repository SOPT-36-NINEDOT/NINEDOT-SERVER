package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;

public record CoreGoalDetailResponse(
    Long id,
    String title,
    int position,
    boolean aiGeneratable
) {

    public static CoreGoalDetailResponse from(CoreGoal coreGoal) {
        return new CoreGoalDetailResponse(coreGoal.getId(),
            coreGoal.getTitle(),
            coreGoal.getPosition(),
            coreGoal.isAiGeneratable()
        );
    }
}
