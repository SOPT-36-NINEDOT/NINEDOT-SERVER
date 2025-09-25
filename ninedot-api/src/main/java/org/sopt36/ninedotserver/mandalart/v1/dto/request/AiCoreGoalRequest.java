package org.sopt36.ninedotserver.mandalart.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AiCoreGoalRequest(
        @NotBlank(message = "만다라트 제목은 필수입니다.")
        String mandalart,
        List<CoreGoalUserInputRequest> coreGoal
        ) {
    public record CoreGoalUserInputRequest(
            String title
    ) {
    }
}
