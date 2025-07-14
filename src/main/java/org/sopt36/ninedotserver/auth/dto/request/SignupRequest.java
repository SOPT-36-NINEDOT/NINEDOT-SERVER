package org.sopt36.ninedotserver.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record SignupRequest(
    @NotBlank(message = "소셜 로그인 유형은 필수입니다")
    String socialProvider,

    @NotBlank(message = "소셜 토큰은 필수입니다")
    String socialToken,

    @NotBlank(message = "이름은 필수입니다.")
    String name,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "^\\d{4}.\\d{2}.\\d{2}$", message = "생년월일 형식이 올바르지 않습니다 (yyyy.MM.dd)")
    String birthday,

    String job,

    String profileImageUrl,

    List<Answer> answers
) {

    public record Answer(Long questionId, int choiceId) {

    }
}
