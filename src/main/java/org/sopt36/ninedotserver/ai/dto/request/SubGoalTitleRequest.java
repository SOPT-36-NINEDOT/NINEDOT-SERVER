package org.sopt36.ninedotserver.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubGoalTitleRequest(
    @NotBlank
    String title
) {

}
