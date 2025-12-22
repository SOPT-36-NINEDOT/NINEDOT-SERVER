package org.sopt36.ninedotserver.mandalart.v1;

import static org.sopt36.ninedotserver.mandalart.v1.message.HistoryMessage.HISTORY_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.v1.message.HistoryMessage.HISTORY_DELETED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.v1.message.HistoryMessage.HISTORY_RETRIEVED_SUCCESS;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.StreakListResponse;
import org.sopt36.ninedotserver.mandalart.usecase.command.HistoryCommandService;
import org.sopt36.ninedotserver.mandalart.usecase.query.HistoryQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class HistoryController {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final HistoryCommandService historyCommandService;
    private final HistoryQueryService historyQueryService;

    @PostMapping("/sub-goals/{subGoalId}/histories")
    public ResponseEntity<ApiResponse<Void, Void>> createHistory(
        @PathVariable Long subGoalId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @AuthenticationPrincipal PrincipalDto principal
    ) {
        Long userId = principal.userId();
        LocalDate completedDate = (date != null) ? date : LocalDate.now(KST);
        Long historyId = historyCommandService.createHistory(userId, subGoalId, completedDate);
        URI location = URI.create(
            "/api/v1/sub-goals/" + subGoalId + "/histories/" + historyId
        );

        return ResponseEntity.created(location)
            .body(ApiResponse.created(HISTORY_CREATED_SUCCESS));
    }

    @DeleteMapping("/sub-goals/{subGoalId}/histories")
    public ResponseEntity<ApiResponse<Void, Void>> deleteHistory(
        @PathVariable Long subGoalId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @AuthenticationPrincipal PrincipalDto principal
    ) {
        Long userId = principal.userId();
        LocalDate completedDate = (date != null) ? date : LocalDate.now(KST);
        historyCommandService.deleteHistory(userId, subGoalId, completedDate);

        return ResponseEntity.ok(ApiResponse.ok(HISTORY_DELETED_SUCCESS));
    }

    @GetMapping("/mandalarts/{mandalartId}/streaks")
    public ResponseEntity<ApiResponse<StreakListResponse, Void>> getStreaks(
        @PathVariable Long mandalartId,
        @AuthenticationPrincipal PrincipalDto principal
    ) {
        Long userId = principal.userId();
        StreakListResponse response = historyQueryService.getStreaks(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(HISTORY_RETRIEVED_SUCCESS, response));
    }
}
