package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.HistoryMessage.HISTORY_CREATED_SUCCESS;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.service.command.HistoryCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.HistoryQueryService;
import org.springframework.http.ResponseEntity;
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
        @PathVariable Long subGoalId
    ) {
        Long userId = 1L;

        Long historyId = historyCommandService.createHistory(userId, subGoalId);
        URI location = URI.create(
            "/api/v1/sub-goals/" + subGoalId + "/histories/" + historyId);

        return ResponseEntity.created(location)
            .body(ApiResponse.created(HISTORY_CREATED_SUCCESS));
    }

}
