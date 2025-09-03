package org.sopt36.ninedotserver.ai.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.sopt36.ninedotserver.ai.dto.response.CoreGoalAiResponse;
import org.sopt36.ninedotserver.ai.exception.AiErrorCode;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.port.AiClient;
import org.sopt36.ninedotserver.ai.support.PromptBuilder;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalSnapshotRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.Question;
import org.sopt36.ninedotserver.onboarding.port.out.AnswerRepositoryPort;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.support.AgeUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiRecommendationService {

    private final MandalartRepositoryPort mandalartRepository;
    private final AnswerRepositoryPort answerRepository;
    private final CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository;
    private final @Qualifier("geminiClient") AiClient geminiClient;
    private final ObjectMapper objectMapper;

    public AiRecommendationService(
        MandalartRepositoryPort mandalartRepository,
        AnswerRepositoryPort answerRepository,
        CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository,
        @Qualifier("geminiClient") AiClient geminiClient,
        ObjectMapper objectMapper
    ) {
        this.mandalartRepository = mandalartRepository;
        this.answerRepository = answerRepository;
        this.coreGoalSnapshotRepository = coreGoalSnapshotRepository;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CoreGoalAiResponse fetchAiRecommendation(Long mandalartId) {
        Mandalart mandalart = mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new AiException(AiErrorCode.MANDALART_NOT_FOUND));
        if (!mandalart.isAiGeneratable()) {
            throw new AiException(AiErrorCode.CORE_GOAL_AI_FEATURE_NOT_AVAILABLE);
        }

        User user = mandalartRepository.findUserById(mandalartId)
            .orElseThrow(() -> new AiException(AiErrorCode.MANDALART_NOT_FOUND));

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

        mandalart.disableAiGeneration();

        return convertAiResponseToDtoResponse(response);
    }

    private List<String> findQuestionByUserId(Long userId) {
        return answerRepository.findQuestionsByUserIdAndDomain(userId,
                Domain.PERSONA)
            .stream()
            .map(Question::getContent)
            .collect(Collectors.toList());
    }

    private List<String> findAnswerContentsByUserId(Long userId) {
        List<String> answers = answerRepository.findAllAnswerContentsByUserId(userId,
            Domain.PERSONA);
        if (answers == null) {
            answers = List.of();
        }
        return answers;
    }

    private List<String> findCoreGoalsByMandalartId(Long mandalartId) {
        List<String> coreGoals = coreGoalSnapshotRepository.findActiveCoreGoalTitleByMandalartId(
            mandalartId);
        if (coreGoals == null) {
            coreGoals = List.of();
        }
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
            throw new AiException(AiErrorCode.AI_RESPONSE_PARSE_ERROR);
        }
    }

}
