package org.sopt36.ninedotserver.mandalart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sopt36.ninedotserver.mandalart.model.Cycle;

public record SubGoalUpdateRequest(
    @NotBlank(message = "title은 필수 입력값입니다.")
    @Size(max = 30, message = "title은 최대 30자까지 입력 가능합니다.")
    String title,

    @NotNull(message = "cycle은 필수 입력값입니다.")
    Cycle cycle
) {

}
