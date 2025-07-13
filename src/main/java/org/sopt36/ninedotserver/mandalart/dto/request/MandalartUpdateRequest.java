package org.sopt36.ninedotserver.mandalart.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MandalartUpdateRequest(
    CoreGoalUpdateWithPositionRequest coreGoal,

    @NotEmpty(message = "하위 목표 요청 내용은 필수입니다.")
    List<SubGoalUpdateWithPositionRequest> subGoals
) {

}
