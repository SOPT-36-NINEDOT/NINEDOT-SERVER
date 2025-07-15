package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public record SubGoalAiListResponse(
    List<SubGoalAiResponse> subGoals
) {

    public static SubGoalAiListResponse of(List<SubGoalSnapshot> subGoals) {
        return new SubGoalAiListResponse(
            subGoals.stream()
                .map(SubGoalAiResponse::from)
                .toList()
        );
    }
}
