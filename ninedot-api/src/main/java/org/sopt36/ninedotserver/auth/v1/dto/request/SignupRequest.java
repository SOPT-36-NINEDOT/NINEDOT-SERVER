package org.sopt36.ninedotserver.auth.v1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;

public record SignupRequest(
    @NotBlank(message = "소셜 로그인 유형은 필수입니다")
    String socialProvider,

    @NotBlank(message = "소셜 토큰은 필수입니다")
    String socialToken,

    @Size(max = 10, message = "이름은 10자 이하로 입력해주세요.")
    @Pattern(
        regexp = "^[가-힣a-zA-Z0-9 ]+$",
        message = "이름에는 특수문자 또는 이모지를 포함할 수 없습니다."
    )//근데 이거 완전차단은 안되긴 하는데.. 그래도 거의 차단해줌
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

    List<SignupCommand.Answer> answers
) {

    public record Answer(Long questionId, int choiceId) {

    }
}
