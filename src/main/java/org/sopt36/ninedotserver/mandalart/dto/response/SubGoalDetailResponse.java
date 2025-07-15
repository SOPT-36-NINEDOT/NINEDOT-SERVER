package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public record SubGoalDetailResponse(
    Long id,
    String title,
    String cycle
) {

    public static SubGoalDetailResponse from(SubGoalSnapshot subGoalSnapshot) {
        return new SubGoalDetailResponse(
            subGoalSnapshot.getId(),
            subGoalSnapshot.getTitle(),
            subGoalSnapshot.getCycle().toString()
        );
    }
}
