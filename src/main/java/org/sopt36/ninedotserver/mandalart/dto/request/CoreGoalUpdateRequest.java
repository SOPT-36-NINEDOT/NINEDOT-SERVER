package org.sopt36.ninedotserver.mandalart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CoreGoalUpdateRequest(
    @NotBlank(message = "title은 필수 항목입니다.")
    @Size(max = 30, message = "title은 최대 30자까지 입력 가능합니다.")
    String title
) {

}
