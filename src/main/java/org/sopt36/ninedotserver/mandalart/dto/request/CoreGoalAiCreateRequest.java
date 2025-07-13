package org.sopt36.ninedotserver.mandalart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CoreGoalAiCreateRequest(
    @NotEmpty(message = "상위 목표 요청 내용은 필수입니다.")
    List<@NotBlank(message = "목표는 공백일 수 없습니다.") String> goals
) {

}
