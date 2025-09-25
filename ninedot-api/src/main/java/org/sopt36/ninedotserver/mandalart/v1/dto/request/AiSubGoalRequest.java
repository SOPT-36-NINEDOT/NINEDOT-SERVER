package org.sopt36.ninedotserver.mandalart.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AiSubGoalRequest(
        @NotBlank(message = "상위 목표 제목은 필수입니다.")
        String coreGoal,
        List<SubGoalUserInputRequest> subGoal
) {
    public record SubGoalUserInputRequest(
            String title
    ) {
    }
}
