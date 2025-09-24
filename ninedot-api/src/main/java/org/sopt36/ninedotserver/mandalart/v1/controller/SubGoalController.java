package org.sopt36.ninedotserver.mandalart.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.dto.query.AiSubGoalQuery;
import org.sopt36.ninedotserver.ai.dto.result.AiSubGoalResult;
import org.sopt36.ninedotserver.ai.usecase.AiSubGoalRecommendationService;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalAiListCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.*;
import org.sopt36.ninedotserver.mandalart.model.Cycle;
import org.sopt36.ninedotserver.mandalart.usecase.command.SubGoalCommandService;
import org.sopt36.ninedotserver.mandalart.usecase.query.SubGoalQueryService;
import org.sopt36.ninedotserver.mandalart.v1.dto.request.AiSubGoalRequest;
import org.sopt36.ninedotserver.mandalart.v1.mapper.AiSubGoalRequestMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.sopt36.ninedotserver.mandalart.v1.message.SubGoalMessage.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class SubGoalController {

    private final SubGoalCommandService subGoalCommandService;
    private final SubGoalQueryService subGoalQueryService;
    private final AiSubGoalRecommendationService aiSubGoalService;

    @GetMapping("/core-goals/{coreGoalId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalIdListResponse, Void>> getSubGoalIds(
            @PathVariable Long coreGoalId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        List<SubGoalIdResponse> subGoalIds = subGoalQueryService.getSubGoalIds(userId, coreGoalId);

        return ResponseEntity.ok(
                ApiResponse.ok(SUB_GOAL_ID_LIST_FETCH_SUCCESS, SubGoalIdListResponse.from(subGoalIds))
        );
    }

    @PostMapping("/core-goals/{coreGoalId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalCreateResponse, Void>> createSubGoal(
            Authentication authentication,
            @PathVariable Long coreGoalId,
            @Valid @RequestBody SubGoalCreateRequest request
    ) {
        Long userId = Long.parseLong(authentication.getName());
        SubGoalCreateResponse response = subGoalCommandService.createSubGoal(
                userId, coreGoalId, request
        );
        URI location = URI.create(
                "/api/v1/core-goals/" + coreGoalId + "/sub-goals/" + response.id()
        );

        return ResponseEntity.created(location)
                .body(ApiResponse.created(response, SUB_GOAL_CREATE_SUCCESS));
    }

    @PatchMapping("/sub-goals/{subGoalId}")
    public ResponseEntity<ApiResponse<Void, Void>> updateSubGoal(
            Authentication authentication,
            @PathVariable Long subGoalId,
            @Valid @RequestBody SubGoalUpdateRequest request
    ) {
        Long userId = Long.parseLong(authentication.getName());
        subGoalCommandService.updateSubGoal(userId, subGoalId, request);

        return ResponseEntity.ok().body(ApiResponse.ok(SUB_GOAL_UPDATE_SUCCESS));
    }

    @DeleteMapping("/sub-goals/{subGoalId}")
    public ResponseEntity<ApiResponse<Void, Void>> deleteSubGoal(
            Authentication authentication,
            @PathVariable Long subGoalId
    ) {
        Long userId = Long.parseLong(authentication.getName());
        subGoalCommandService.deleteSubGoal(userId, subGoalId);

        return ResponseEntity.ok().body(ApiResponse.ok(SUB_GOAL_DELETE_SUCCESS));
    }

    @PostMapping("/core-goals/{coreGoalId}/ai")
    public ResponseEntity<ApiResponse<AiSubGoalResult, Void>> generateSubGoalByAi(
            Authentication authentication,
            @PathVariable("coreGoalId") Long coreGoalId,
            @RequestBody @Valid AiSubGoalRequest request
    ) {
        Long userId = Long.parseLong(authentication.getName());
        AiSubGoalQuery query = AiSubGoalRequestMapper.toAiSubGoalQuery(request);
        AiSubGoalResult response = aiSubGoalService.fetchAiSubGoalRecommendation(
                userId,
                coreGoalId,
                query
        );
        return ResponseEntity.ok(ApiResponse.ok(SUB_GOAL_AI_RECOMMENDATION_SUCCESS, response));
    }

    @GetMapping("/mandalarts/{mandalartId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalListResponse, Void>> getSubGoal(
            Authentication authentication,
            @PathVariable Long mandalartId,
            @RequestParam(required = false) Long coreGoalId,
            @RequestParam(required = false) Cycle cycle,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = Long.parseLong(authentication.getName());
        LocalDate targetDate = (date != null ? date : LocalDate.now());

        SubGoalListResponse response = subGoalQueryService.getSubGoalWithFilter(
                userId,
                mandalartId,
                coreGoalId,
                cycle,
                targetDate
        );

        return ResponseEntity.ok(ApiResponse.ok(SUB_GOAL_READ_SUCCESS, response));
    }

    @PostMapping("/core-goals/{coreGoalId}/sub-goals/ai")
    public ResponseEntity<ApiResponse<SubGoalAiListResponse, Void>> addAiSubGoals(
            Authentication authentication,
            @PathVariable Long coreGoalId,
            @Valid @RequestBody SubGoalAiListCreateRequest aiCreateRequest
    ) {
        Long userId = Long.parseLong(authentication.getName());
        SubGoalAiListResponse response = subGoalCommandService.createAiSubGoals(
                userId,
                coreGoalId,
                aiCreateRequest
        );

        return ResponseEntity.ok(ApiResponse.ok(SUB_GOAL_AI_CREATED_SUCCESS, response));
    }

}
