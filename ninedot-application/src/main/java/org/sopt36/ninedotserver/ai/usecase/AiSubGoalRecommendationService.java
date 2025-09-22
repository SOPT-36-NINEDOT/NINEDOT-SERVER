package org.sopt36.ninedotserver.ai.usecase;

import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalAiRequest;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalRecommendationRequest;
import org.sopt36.ninedotserver.ai.dto.response.SubGoalAiResponse;
import org.sopt36.ninedotserver.ai.exception.AiErrorCode;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.port.AiClient;
import org.sopt36.ninedotserver.ai.support.PromptBuilder;
import org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode;
import org.sopt36.ninedotserver.mandalart.exception.CoreGoalException;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalSnapshotRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.onboarding.port.out.AnswerRepositoryPort;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiSubGoalRecommendationService {

    private final MandalartRepositoryPort mandalartRepository;
    private final UserQueryPort userRepository;
    private final AnswerRepositoryPort answerRepository;
    private final @Qualifier("geminiSubgoalClient") AiClient geminiSubGoalClient;
    private final CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository;
    private final ObjectMapper objectMapper;

    public AiSubGoalRecommendationService(
        MandalartRepositoryPort mandalartRepository,
        UserQueryPort userRepository,
        AnswerRepositoryPort answerRepository,
        @Qualifier("geminiSubgoalClient") AiClient geminiSubGoalClient,
        CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository,
        ObjectMapper objectMapper
    ) {
        this.mandalartRepository = mandalartRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.geminiSubGoalClient = geminiSubGoalClient;
        this.coreGoalSnapshotRepository = coreGoalSnapshotRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SubGoalAiResponse fetchAiSubGoalRecommendation(
        Long userId,
        Long coreGoalSnapshotId,
        SubGoalAiRequest request
    ) {
        CoreGoalSnapshot coreGoalSnapshot = getExistingCoreGoal(coreGoalSnapshotId);

        if (request.subGoal().size() >= 8) {
            throw new AiException(AiErrorCode.SUB_GOAL_IS_FULL);
        }

        if (!coreGoalSnapshot.getCoreGoal().isAiGeneratable()) {
            throw new AiException(AiErrorCode.SUB_GOAL_AI_FEATURE_NOT_AVAILABLE);
        }

        String mandalartTitle = mandalartRepository.findTitleByCoreGoalId(coreGoalSnapshotId)
            .orElseThrow(() -> new AiException(AiErrorCode.MANDALART_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AiException(USER_NOT_FOUND));

        int age = user.getBirthday().calculateAge();

        Map<String, String> questionAnswerMap = answerRepository.findQnAMapByUserId(user.getId());

        String coreGoalTitle = request.coreGoal();
        List<String> existingSubGoals = request.subGoal().stream()
            .map(SubGoalRecommendationRequest::title)
            .toList();

        String prompt = PromptBuilder.buildSubGoalPrompt(
            age,
            user.jobAsString(),
            mandalartTitle,
            coreGoalTitle,
            questionAnswerMap,
            existingSubGoals
        );

        String response = geminiSubGoalClient.fetchAiResponse(prompt);

        coreGoalSnapshot.getCoreGoal().disableAiGeneration();

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
            throw new AiException(AiErrorCode.AI_RESPONSE_PARSE_ERROR);
        }
    }

    private CoreGoalSnapshot getExistingCoreGoal(Long coreGoalId) {
        return coreGoalSnapshotRepository.findById(coreGoalId)
            .orElseThrow(() -> new CoreGoalException(CoreGoalErrorCode.CORE_GOAL_NOT_FOUND));
    }
}
