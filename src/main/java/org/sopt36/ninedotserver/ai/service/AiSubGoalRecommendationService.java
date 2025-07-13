package org.sopt36.ninedotserver.ai.service;

import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.AI_RESPONSE_PARSE_ERROR;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.client.GeminiClient;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalAiRequest;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalRecommendationRequest;
import org.sopt36.ninedotserver.ai.dto.response.SubGoalAiResponse;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.util.AgeUtil;
import org.sopt36.ninedotserver.ai.util.PromptBuilder;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.onboarding.repository.AnswerRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiSubGoalRecommendationService {

    private final MandalartRepository mandalartRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public SubGoalAiResponse fetchAiSubGoalRecommendation(Long coreGoalId, Long userId,
        SubGoalAiRequest request) {
        String mandalartTitle = mandalartRepository.findTitleByCoreGoalId(coreGoalId)
            .orElseThrow(() -> new AiException(MANDALART_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AiException(USER_NOT_FOUND));

        int age = AgeUtil.calculateAgeFromString(user.getBirthday());

        Map<String, String> questionAnswerMap = answerRepository.findQnAMapByUserId(user.getId());

        String coreGoalTitle = request.coreGoal();
        List<String> existingSubGoals = request.subGoal().stream()
            .map(SubGoalRecommendationRequest::title)
            .toList();

        String prompt = PromptBuilder.buildSubGoalPrompt(
            age,
            user.getJob().getDisplayName(),
            mandalartTitle,
            request.coreGoal(),
            questionAnswerMap,
            existingSubGoals
        );

        String response = geminiClient.fetchAiResponse(prompt);

        return convertToSubGoalResponse(response);

    }

    private SubGoalAiResponse convertToSubGoalResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String innerJson = root
                .path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

            return objectMapper.readValue(innerJson, SubGoalAiResponse.class);
        } catch (JsonProcessingException e) {
            throw new AiException(AI_RESPONSE_PARSE_ERROR);
        }
    }
}
