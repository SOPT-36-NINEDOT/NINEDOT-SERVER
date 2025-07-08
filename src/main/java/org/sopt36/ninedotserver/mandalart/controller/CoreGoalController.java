package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.CoreGoalMessage.CREATED_SUCCESS;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.service.command.CoreGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.CoreGoalQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class CoreGoalController {

    private final CoreGoalCommandService coreGoalCommandService;
    private final CoreGoalQueryService coreGoalQueryService;

    // TODO 만다라트 상위 목표 생성
    @PostMapping("/mandalarts/{mandalartId}/core-goals")
    public ResponseEntity<ApiResponse<CoreGoalCreateResponse, Void>> createCoreGoal(
        @PathVariable Long mandalartId,
        @Valid @RequestBody CoreGoalCreateRequest createRequest
    ) {
        Long userId = 1L; // TODO 로그인 구현 완료 후 Authentication 에서 가져오도록 변경
        CoreGoalCreateResponse response = coreGoalCommandService.createCoreGoal(userId,
            mandalartId,
            createRequest
        );
        Long coreGoalId = response.id();
        URI location = URI.create("/api/v1/mandalarts/" + mandalartId + "/core-goals" + coreGoalId);

        return ResponseEntity.created(location)
            .body(ApiResponse.created(response, CREATED_SUCCESS));
    }

}
