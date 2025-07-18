package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public record SubGoalDetailResponse(
    Long id,
    String title,
    String cycle,
    boolean isCompleted
) {

    public static SubGoalDetailResponse of(SubGoalSnapshot subGoalSnapshot, boolean isCompleted) {
        return new SubGoalDetailResponse(
            subGoalSnapshot.getId(),
            subGoalSnapshot.getTitle(),
            subGoalSnapshot.getCycle().toString(),
            isCompleted
        );
    }
}
