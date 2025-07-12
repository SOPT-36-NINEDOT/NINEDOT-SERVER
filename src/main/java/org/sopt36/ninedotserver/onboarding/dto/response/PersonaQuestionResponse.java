package org.sopt36.ninedotserver.onboarding.dto.response;

import java.util.List;

public record PersonaQuestionResponse(
    List<QuestionResponse> questionList
) {

    public static PersonaQuestionResponse from(List<QuestionResponse> questions) {
        return new PersonaQuestionResponse(questions);
    }

}


