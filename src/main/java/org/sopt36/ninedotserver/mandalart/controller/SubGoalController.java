package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_CREATE_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.SubGoalMessage.SUB_GOAL_ID_LIST_FETCH_SUCCESS;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.service.command.SubGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.SubGoalQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/core-goals")
@RequiredArgsConstructor
@RestController
public class SubGoalController {

    private final SubGoalCommandService subGoalCommandService;
    private final SubGoalQueryService subGoalQueryService;

    @GetMapping("/{coreGoalId}/sub-goals")
    public ResponseEntity<ApiResponse<SubGoalIdListResponse, Void>> getSubGoalIds(
        @PathVariable Long coreGoalId
    ) {
        Long userId = 1L; // TODO: 로그인 구현되면 token에서 사용자id 가져오기
        List<SubGoalIdResponse> subGoalIds = subGoalQueryService.getSubGoalIds(userId, coreGoalId);
        return ResponseEntity.ok()
            .body(ApiResponse.ok(
                    SUB_GOAL_ID_LIST_FETCH_SUCCESS,
                    SubGoalIdListResponse.from(subGoalIds)
                )
            );
    }

    @PostMapping("/{coreGoalId}/sub-goals")
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
}
