package org.sopt36.ninedotserver.auth.dto.request;

import java.util.List;

public record SignupRequest(
    String socialProvider,
    String socialToken,
    String name,
    String email,
    String birthday,
    String job,
    String profileImageUrl,
    List<Answer> answers
) {

    public record Answer(Long questionId, int choiceId) {

    }
}
