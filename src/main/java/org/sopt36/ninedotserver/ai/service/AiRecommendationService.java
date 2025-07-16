package org.sopt36.ninedotserver.ai.service;

import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.AI_RESPONSE_PARSE_ERROR;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.ANSWER_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.CORE_GOAL_AI_FEATURE_NOT_AVAILABLE;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.QUESTION_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.client.GeminiClient;
import org.sopt36.ninedotserver.ai.dto.response.CoreGoalAiResponse;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.util.AgeUtil;
import org.sopt36.ninedotserver.ai.util.PromptBuilder;
import org.sopt36.ninedotserver.global.exception.ErrorCode;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalSnapshotRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.onboarding.domain.Domain;
import org.sopt36.ninedotserver.onboarding.domain.Question;
import org.sopt36.ninedotserver.onboarding.repository.AnswerRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AiRecommendationService {

    private final MandalartRepository mandalartRepository;
    private final AnswerRepository answerRepository;
    private final CoreGoalSnapshotRepository coreGoalSnapshotRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public CoreGoalAiResponse fetchAiRecommendation(Long mandalartId) {
        Mandalart mandalart = mandalartRepository.findById(mandalartId)
                                  .orElseThrow(() -> new AiException(MANDALART_NOT_FOUND));
        if (!mandalart.isAiGeneratable()) {
            throw new AiException(CORE_GOAL_AI_FEATURE_NOT_AVAILABLE);
        }

        User user = mandalartRepository.findUserById(mandalartId)
                        .orElseThrow(() -> new AiException(MANDALART_NOT_FOUND));

        int age = AgeUtil.calculateAgeFromString(user.getBirthday());

        List<String> questions = findQuestionByUserId(user.getId());

        List<String> answers = findAnswerContentsByUserId(user.getId());

        String mandalartTitle = mandalart.getTitle();

        List<String> coreGoals = findCoreGoalsByMandalartId(mandalartId);

        String prompt = PromptBuilder.buildCoreGoalPrompt(
            age,
            user.getJob(),
            questions,
            answers,
            mandalartTitle,
            coreGoals);

        String response = geminiClient.fetchAiResponse(prompt);

        mandalart.setAiGeneratable(false);

        return convertAiResponseToDtoResponse(response);
    }

    private void isListEmpty(List<?> list, ErrorCode errorCode) {
        if (list == null || list.isEmpty()) {
            throw new AiException(errorCode);
        }
    }

    private List<String> findQuestionByUserId(Long userId) {
        List<String> questions = answerRepository.findQuestionsByUserIdAndDomain(userId,
                Domain.PERSONA)
                                     .stream()
                                     .map(Question::getContent)
                                     .collect(Collectors.toList());
        isListEmpty(questions, QUESTION_NOT_FOUND);
        return questions;
    }

    private List<String> findAnswerContentsByUserId(Long userId) {
        List<String> answers = answerRepository.findAllAnswerContentsByUserId(userId);
        isListEmpty(answers, ANSWER_NOT_FOUND);
        return answers;
    }

    private List<String> findCoreGoalsByMandalartId(Long mandalartId) {
        List<String> coreGoals = coreGoalSnapshotRepository.findActiveCoreGoalTitleByMandalartId(
            mandalartId);
        isListEmpty(coreGoals, CORE_GOAL_NOT_FOUND);
        return coreGoals;
    }

    private CoreGoalAiResponse convertAiResponseToDtoResponse(String response) {
        try {
            //object mapper의 Read tree는 json 구조를 갖고 자유롭게 놀 수 있게 해주는 친구
            JsonNode root = objectMapper.readTree(response);
            //이걸로 ai의 전체 응답 중 우리가 원했던 응답만 쏙 빼온다.
            String innerJson = root
                                   .path("candidates").get(0)
                                   .path("content").path("parts").get(0)
                                   .path("text").asText();

            //Read value는 그 json 응답을 dto랑 매칭시켜서 바로 포장해준다.
            return objectMapper.readValue(innerJson, CoreGoalAiResponse.class);
        } catch (JsonProcessingException e) {
            throw new AiException(AI_RESPONSE_PARSE_ERROR);
        }
    }

}
