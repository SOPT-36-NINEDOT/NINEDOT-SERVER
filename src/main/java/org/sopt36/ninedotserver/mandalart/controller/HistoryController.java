package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_DELETED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_RETRIEVED_SUCCESS;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.StreakListResponse;
import org.sopt36.ninedotserver.mandalart.service.command.HistoryCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.HistoryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class HistoryController {

    private final HistoryCommandService historyCommandService;
    private final HistoryQueryService historyQueryService;

    @PostMapping("/sub-goals/{subGoalId}/histories")
    public ResponseEntity<ApiResponse<Void, Void>> createHistory(
        Authentication authentication,
        @PathVariable Long subGoalId
    ) {
        Long userId = Long.parseLong(authentication.getName());
        Long historyId = historyCommandService.createHistory(userId, subGoalId);
        URI location = URI.create(
            "/api/v1/sub-goals/" + subGoalId + "/histories/" + historyId);

        return ResponseEntity.created(location)
            .body(ApiResponse.created(HISTORY_CREATED_SUCCESS));
    }

    @DeleteMapping("/sub-goals/{subGoalId}/histories")
    public ResponseEntity<ApiResponse<Void, Void>> deleteHistory(
        Authentication authentication,
        @PathVariable Long subGoalId
    ) {
        Long userId = Long.parseLong(authentication.getName());
        historyCommandService.deleteHistory(userId, subGoalId);

        return ResponseEntity.ok(ApiResponse.ok(HISTORY_DELETED_SUCCESS));
    }

    @GetMapping("/mandalarts/{mandalartId}/streaks")
    public ResponseEntity<ApiResponse<StreakListResponse, Void>> getStreaks(
        Authentication authentication,
        @PathVariable Long mandalartId
    ) {
        Long userId = Long.parseLong(authentication.getName());
        StreakListResponse response = historyQueryService.getStreaks(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(HISTORY_RETRIEVED_SUCCESS, response));
    }
}
