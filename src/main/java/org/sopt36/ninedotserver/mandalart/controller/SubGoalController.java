package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_AI_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_AI_RECOMMENDATION_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_CREATE_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_DELETE_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_ID_LIST_FETCH_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_READ_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_UPDATE_SUCCESS;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.dto.request.SubGoalAiRequest;
import org.sopt36.ninedotserver.ai.dto.response.SubGoalAiResponse;
import org.sopt36.ninedotserver.ai.service.AiSubGoalRecommendationService;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalAiListCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalAiListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.service.command.SubGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.SubGoalQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class SubGoalController {

    private final SubGoalCommandService subGoalCommandService;
    private final SubGoalQueryService subGoalQueryService;
    private final AiSubGoalRecommendationService aiSubGoalService;

    @GetMapping("/core-goals/{coreGoalId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalIdListResponse, Void>> getSubGoalIds(
        @PathVariable Long coreGoalId
    ) {
        Long userId = 1L;
        List<SubGoalIdResponse> subGoalIds = subGoalQueryService.getSubGoalIds(userId, coreGoalId);

        return ResponseEntity.ok()
            .body(
                ApiResponse.ok(SUB_GOAL_ID_LIST_FETCH_SUCCESS,
                    SubGoalIdListResponse.from(subGoalIds))
            );
    }

    @PostMapping("/core-goals/{coreGoalId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalCreateResponse, Void>> createSubGoal(
        @PathVariable Long coreGoalId,
        @Valid @RequestBody SubGoalCreateRequest request
    ) {
        Long userId = 1L;
        SubGoalCreateResponse response = subGoalCommandService.createSubGoal(
            userId,
            coreGoalId,
            request
        );
        URI location = URI.create(
            "/api/v1/core-goals/" + coreGoalId + "/sub-goals/" + response.id());

        return ResponseEntity.created(location)
            .body(ApiResponse.created(response, SUB_GOAL_CREATE_SUCCESS));
    }

    @PatchMapping("/sub-goals/{subGoalId}")
    public ResponseEntity<ApiResponse<Void, Void>> updateSubGoal(
        @PathVariable Long subGoalId,
        @Valid @RequestBody SubGoalUpdateRequest request
    ) {
        Long userId = 1L;
        subGoalCommandService.updateSubGoal(userId, subGoalId, request);

        return ResponseEntity.ok()
            .body(ApiResponse.ok(SUB_GOAL_UPDATE_SUCCESS));
    }

    @DeleteMapping("/sub-goals/{subGoalId}")
    public ResponseEntity<ApiResponse<Void, Void>> deleteSubGoal(
        @PathVariable Long subGoalId
    ) {
        Long userId = 1L;
        subGoalCommandService.deleteSubGoal(userId, subGoalId);

        return ResponseEntity.ok()
            .body(ApiResponse.ok(SUB_GOAL_DELETE_SUCCESS));

    }

    @PostMapping("/core-goals/{coreGoalId}/ai")
    public ResponseEntity<ApiResponse<SubGoalAiResponse, Void>> generateSubGoalByAi(
        @PathVariable("coreGoalId") Long coreGoalId,
        @RequestBody @Valid SubGoalAiRequest request
    ) {
        Long userId = 1L;
        SubGoalAiResponse response = aiSubGoalService.fetchAiSubGoalRecommendation(userId,
            coreGoalId,
            request);
        return ResponseEntity.ok(ApiResponse.ok(SUB_GOAL_AI_RECOMMENDATION_SUCCESS, response));
    }

    @GetMapping("/mandalarts/{mandalartId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalListResponse, Void>> getSubGoal(
        @PathVariable Long mandalartId,
        @RequestParam(required = false) Long coreGoalId,
        @RequestParam(required = false) Cycle cycle,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = 1L;
        SubGoalListResponse response = subGoalQueryService.getSubGoalWithFilter(userId, mandalartId,
            coreGoalId, cycle, date);
        return ResponseEntity.ok(ApiResponse.ok(SUB_GOAL_READ_SUCCESS, response));
    }

    @PostMapping("/core-goals/{coreGoalId}/sub-goals/ai")
    public ResponseEntity<ApiResponse<SubGoalAiListResponse, Void>> addAiSubGoals(
        @PathVariable Long coreGoalId,
        @Valid @RequestBody SubGoalAiListCreateRequest aiCreateRequest
    ) {
        Long userId = 1L;
        SubGoalAiListResponse response = subGoalCommandService
            .createAiSubGoals(userId, coreGoalId, aiCreateRequest);

        return ResponseEntity.ok(ApiResponse.ok(SUB_GOAL_AI_CREATED_SUCCESS, response));
    }

}
