package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_DELETED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_RETRIEVED_SUCCESS;

import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.StreakListResponse;
import org.sopt36.ninedotserver.mandalart.service.command.HistoryCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.HistoryQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
        @PathVariable Long subGoalId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = 1L;
        LocalDate completedDate = (date != null) ? date : LocalDate.now();
        Long historyId = historyCommandService.createHistory(userId, subGoalId, completedDate);
        URI location = URI.create(
            "/api/v1/sub-goals/" + subGoalId + "/histories/" + historyId);

        return ResponseEntity.created(location)
            .body(ApiResponse.created(HISTORY_CREATED_SUCCESS));
    }

    @DeleteMapping("/sub-goals/{subGoalId}/histories")
    public ResponseEntity<ApiResponse<Void, Void>> deleteHistory(
        @PathVariable Long subGoalId
    ) {
        Long userId = 1L;
        historyCommandService.deleteHistory(userId, subGoalId);

        return ResponseEntity.ok(ApiResponse.ok(HISTORY_DELETED_SUCCESS));
    }

    @GetMapping("/mandalarts/{mandalartId}/streaks")
    public ResponseEntity<ApiResponse<StreakListResponse, Void>> getStreaks(
        @PathVariable Long mandalartId
    ) {
        Long userId = 1L;
        StreakListResponse response = historyQueryService.getStreaks(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(HISTORY_RETRIEVED_SUCCESS, response));
    }
}
