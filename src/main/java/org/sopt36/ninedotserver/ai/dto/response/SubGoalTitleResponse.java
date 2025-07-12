package org.sopt36.ninedotserver.ai.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;

public record SubGoalTitleResponse(
    @NotBlank
    String title,

    @NotNull
    Cycle cycle
) {

}
