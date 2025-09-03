package org.sopt36.ninedotserver.onboarding.dto.response;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.model.Choice;
import org.sopt36.ninedotserver.onboarding.model.Question;
import org.sopt36.ninedotserver.onboarding.model.Type;

public record QuestionResponse(
    Long id,
    Type type,
    String content,
    List<ChoiceResponse> optionList
) {

    public static QuestionResponse from(Question question, List<Choice> choices) {
        return new QuestionResponse(
            question.getId(),
            question.getType(),
            question.getContent(),
            choices.stream()
                .map(choice -> new ChoiceResponse(choice.getId(), choice.getContent()))
                .toList()
        );
    }

    public record ChoiceResponse(
        Long id,
        String content
    ) {

    }
}
