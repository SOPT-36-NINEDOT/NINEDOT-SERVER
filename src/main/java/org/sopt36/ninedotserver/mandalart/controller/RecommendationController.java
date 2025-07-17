package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.RecommendationMessage.RECOMMENDATION_CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.RecommendationMessage.RECOMMENDATION_RETRIEVED_SUCCESS;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.service.command.RecommendationSchedulerService;
import org.sopt36.ninedotserver.mandalart.service.query.RecommendationQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class RecommendationController {

    private final RecommendationQueryService recommendationQueryService;
    private final RecommendationSchedulerService schedulerService;

    @GetMapping("/mandalarts/{mandalartId}/histories/recommendation")
    public ResponseEntity<ApiResponse<SubGoalListResponse, Void>> getRecommendations(
        @PathVariable Long mandalartId,
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = 1L;
        LocalDate recommendationDate = (date != null ? date : LocalDate.now());

        SubGoalListResponse response = recommendationQueryService
            .getRecommendations(userId, mandalartId,
                recommendationDate);

        return ResponseEntity.ok(ApiResponse.ok(RECOMMENDATION_RETRIEVED_SUCCESS, response));
    }


    @PostMapping("/mandalarts/{mandalartId}/onboarding/recommendation")
    public ResponseEntity<ApiResponse<Void, Void>> createRecommendation(
        @PathVariable Long mandalartId
    ) {
        Long userId = 1L;
        schedulerService.computeRecommendations(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(RECOMMENDATION_CREATED_SUCCESS));
    }

}
