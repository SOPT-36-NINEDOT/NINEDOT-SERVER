package org.sopt36.ninedotserver.ai.service;

import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.AI_RESPONSE_PARSE_ERROR;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.SUB_GOAL_AI_FEATURE_NOT_AVAILABLE;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.SUB_GOAL_IS_FULL;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.client.GeminiSubGoalClient;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalAiRequest;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalRecommendationRequest;
import org.sopt36.ninedotserver.ai.dto.response.SubGoalAiResponse;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.util.AgeUtil;
import org.sopt36.ninedotserver.ai.util.PromptBuilder;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.onboarding.repository.AnswerRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiSubGoalRecommendationService {

    private final MandalartRepository mandalartRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final GeminiSubGoalClient geminiSubGoalClient;
    private final CoreGoalRepository coreGoalRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SubGoalAiResponse fetchAiSubGoalRecommendation(Long userId, Long coreGoalId,
        SubGoalAiRequest request) {

        CoreGoal coreGoal = coreGoalRepository.findById(coreGoalId)
                                .orElseThrow(() -> new AiException(CORE_GOAL_NOT_FOUND));

        if (request.subGoal().size() >= 8) {
            throw new AiException(SUB_GOAL_IS_FULL);
        }

        if (!coreGoal.isAiGeneratable()) {
            throw new AiException(SUB_GOAL_AI_FEATURE_NOT_AVAILABLE);
        }

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
            user.getJob(),
            mandalartTitle,
            coreGoalTitle,
            questionAnswerMap,
            existingSubGoals
        );

        String response = geminiSubGoalClient.fetchAiResponse(prompt);

        coreGoal.disableAiGeneration();

        return convertToSubGoalResponse(response);

    }

    private SubGoalAiResponse convertToSubGoalResponse(String response) {
        try {
            // 1. Gemini JSON 응답 전체 파싱
            JsonNode root = objectMapper.readTree(response);

            // 2. text 필드의 실제 값 추출 (JSON 형식의 문자열)
            String innerJsonString = root
                                         .path("candidates").get(0)
                                         .path("content").path("parts").get(0)
                                         .path("text").asText();

            // 3. innerJsonString은 JSON string → JsonNode로 다시 파싱
            JsonNode fixedNode = objectMapper.readTree(innerJsonString);

            // 4. JsonNode → DTO 매핑 (이제 한글 정상)
            return objectMapper.treeToValue(fixedNode, SubGoalAiResponse.class);

        } catch (JsonProcessingException e) {
            throw new AiException(AI_RESPONSE_PARSE_ERROR);
        }
    }

}
