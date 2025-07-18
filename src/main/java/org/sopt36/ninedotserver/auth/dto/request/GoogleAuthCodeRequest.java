package org.sopt36.ninedotserver.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthCodeRequest(
    @NotBlank(message = "인증 코드를 입력해주세요.")
    String code
) {

}
