package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.AI_RESPONSE_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_AI_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_IDS_RETRIEVED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_LIST_RETRIEVED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_ONBOARDING_DELETED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_ONBOARDING_UPDATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CORE_GOAL_UPDATED_WITH_SUB_GOALS_SUCCESS;

import jakarta.validation.Valid;
import java.net.URI;
import org.sopt36.ninedotserver.ai.dto.response.CoreGoalAiResponse;
import org.sopt36.ninedotserver.ai.service.AiRecommendationService;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalAiCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.GenerateCoreGoalRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.MandalartUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalAiListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalIdsResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalsResponse;
import org.sopt36.ninedotserver.mandalart.service.command.CoreGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.CoreGoalQueryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RequestMapping("/api/v1")
@RestController
public class CoreGoalController {

    private final RestClient restClient;
    private final CoreGoalCommandService coreGoalCommandService;
    private final CoreGoalQueryService coreGoalQueryService;
    private final AiRecommendationService aiRecommendationService;

    @Value("${gemini.api.url}")
    private String apiUrl;
    @Value("${gemini.api.key}")
    private String apiKey;

    public CoreGoalController(
        @Qualifier("geminiRestClient") RestClient restClient,
        CoreGoalCommandService coreGoalCommandService,
        CoreGoalQueryService coreGoalQueryService,
        AiRecommendationService aiRecommendationService
    ) {
        this.restClient = restClient;
        this.coreGoalCommandService = coreGoalCommandService;
        this.coreGoalQueryService = coreGoalQueryService;
        this.aiRecommendationService = aiRecommendationService;
    }

    @PostMapping("/onboarding/mandalarts/{mandalartId}/core-goals")
    public ResponseEntity<ApiResponse<CoreGoalCreateResponse, Void>> createCoreGoal(
        @PathVariable Long mandalartId,
        @Valid @RequestBody CoreGoalCreateRequest createRequest,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        CoreGoalCreateResponse response = coreGoalCommandService.createCoreGoal(userId,
            mandalartId,
            createRequest
        );
        Long coreGoalId = response.id();
        URI location = URI.create(
            "/api/v1/mandalarts/" + mandalartId + "/core-goals/" + coreGoalId);

        return ResponseEntity.created(location)
            .body(ApiResponse.created(response, CORE_GOAL_CREATED_SUCCESS));
    }

    @GetMapping("/mandalarts/{mandalartId}/core-goals/id-positions")
    public ResponseEntity<ApiResponse<CoreGoalIdsResponse, Void>> getCoreGoalIds(
        @PathVariable Long mandalartId,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        CoreGoalIdsResponse response = coreGoalQueryService.getCoreGoalIds(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(CORE_GOAL_IDS_RETRIEVED_SUCCESS, response));
    }

    @GetMapping("/onboarding/mandalarts/{mandalartId}/core-goals")
    public ResponseEntity<ApiResponse<CoreGoalsResponse, Void>> getCoreGoals(
        @PathVariable Long mandalartId,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        CoreGoalsResponse response = coreGoalQueryService.getCoreGoals(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(CORE_GOAL_LIST_RETRIEVED_SUCCESS, response));
    }

    @PatchMapping("/onboarding/core-goals/{coreGoalId}")
    public ResponseEntity<ApiResponse<Void, Void>> updateCoreGoal(
        @PathVariable Long coreGoalId,
        @Valid @RequestBody CoreGoalUpdateRequest updateRequest,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        coreGoalCommandService.updateCoreGoal(userId, coreGoalId, updateRequest);

        return ResponseEntity.ok(ApiResponse.ok(CORE_GOAL_ONBOARDING_UPDATED_SUCCESS));
    }

    @DeleteMapping("/onboarding/core-goals/{coreGoalId}")
    public ResponseEntity<ApiResponse<Void, Void>> deleteCoreGoal(
        @PathVariable Long coreGoalId,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        coreGoalCommandService.deleteCoreGoal(userId, coreGoalId);

        return ResponseEntity.ok(ApiResponse.ok(CORE_GOAL_ONBOARDING_DELETED_SUCCESS));
    }

    @PostMapping("/mandalarts/{mandalartId}/core-goals/ai")
    public ResponseEntity<ApiResponse<CoreGoalAiListResponse, Void>> createAiCoreGoals(
        @PathVariable Long mandalartId,
        @Valid @RequestBody CoreGoalAiCreateRequest aiCreateRequest,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        CoreGoalAiListResponse response = coreGoalCommandService.createAiCoreGoals(
            userId,
            mandalartId,
            aiCreateRequest
        );

        return ResponseEntity.ok(ApiResponse.ok(CORE_GOAL_AI_CREATED_SUCCESS, response));
    }

    @PostMapping("/mandalarts/{mandalartId}/ai")
    public ResponseEntity<ApiResponse<CoreGoalAiResponse, Void>> createAi(
        @PathVariable Long mandalartId,
        @RequestBody GenerateCoreGoalRequest generateRequest,
        Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        CoreGoalAiResponse response = aiRecommendationService.fetchAiRecommendation(mandalartId);

        return ResponseEntity.ok(ApiResponse.created(response, AI_RESPONSE_SUCCESS));
    }

    @PatchMapping("/mandalarts/{mandalartId}/core-goals")
    public ResponseEntity<ApiResponse<Void, Void>> updateMandalart(
        @PathVariable Long mandalartId,
        @Valid @RequestBody MandalartUpdateRequest updateRequest,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        coreGoalCommandService.updateMandalart(userId, mandalartId, updateRequest);

        return ResponseEntity.ok(ApiResponse.ok(CORE_GOAL_UPDATED_WITH_SUB_GOALS_SUCCESS));
    }
}
