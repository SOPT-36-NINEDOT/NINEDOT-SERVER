package org.sopt36.ninedotserver.mandalart.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CoreGoalUpdateWithPositionRequest(
    @Min(value = 1, message = "position은 1 이상 8 이하의 값이어야 합니다.")
    @Max(value = 8, message = "position은 1 이상 8 이하의 값이어야 합니다.")
    int position,

    @NotBlank(message = "title은 필수 입력 항목입니다.")
    @Size(max = 30, message = "title은 최대 30자까지 입력 가능합니다.")
    String title
) {

}
