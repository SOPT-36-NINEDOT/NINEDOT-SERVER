package org.sopt36.ninedotserver.ai.service;

import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.ANSWER_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.QUESTION_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.ai.client.GeminiClient;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.util.AgeUtil;
import org.sopt36.ninedotserver.ai.util.PromptBuilder;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.onboarding.domain.Question;
import org.sopt36.ninedotserver.onboarding.repository.AnswerRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiRecommendationService {

    private final MandalartRepository mandalartRepository;
    private final AnswerRepository answerRepository;
    private final GeminiClient geminiClient;

    public String fetchAiRecommendation(Long mandalartId) {
        User user = mandalartRepository.findUserById(mandalartId)
                        .orElseThrow(() -> new AiException(MANDALART_NOT_FOUND));
        int age = AgeUtil.calculateAgeFromString(user.getBirthday());

        List<String> questions = answerRepository.findAllQuestionByUserId((user.getId())).stream()
                                     .map(Question::getContent)
                                     .collect(Collectors.toList());
        isListEmpty(questions, QUESTION_NOT_FOUND);

        List<String> answers = answerRepository.findAllAnswerContentsByUserId((user.getId()));
        isListEmpty(answers, ANSWER_NOT_FOUND);

        String mandalart = mandalartRepository.findTitleByMandalartId(mandalartId)
                               .orElseThrow(() -> new AiException(MANDALART_NOT_FOUND));

        String prompt = PromptBuilder.buildCoreGoalPrompt(age, user.getJob().getDisplayName(),
            questions, answers,
            mandalart, "매일 운동하기", "3대 300찍기");
        return geminiClient.fetchAiResponse(prompt);
    }

    public void isListEmpty(List<?> list, ErrorCode errorCode) {
        if (list == null || list.isEmpty()) {
            throw new AiException(errorCode);
        }
    }

}
