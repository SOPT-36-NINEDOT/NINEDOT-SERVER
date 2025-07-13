package org.sopt36.ninedotserver.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SubGoalAiRequest(
    @NotBlank
    String coreGoal,

    @NotEmpty
    List<SubGoalTitleRequest> subGoal

) {

}
