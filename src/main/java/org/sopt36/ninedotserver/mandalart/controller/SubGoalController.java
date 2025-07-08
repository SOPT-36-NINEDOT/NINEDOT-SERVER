package org.sopt36.ninedotserver.mandalart.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.service.command.SubGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.SubGoalQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                "성공적으로 해당 상위 목표의 하위 목표 ID 리스트를 조회했습니다.",
                SubGoalIdListResponse.from(subGoalIds)
            )
        );
    }
}
