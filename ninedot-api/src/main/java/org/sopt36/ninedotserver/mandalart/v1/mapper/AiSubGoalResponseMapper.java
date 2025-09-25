package org.sopt36.ninedotserver.mandalart.v1.mapper;

import org.sopt36.ninedotserver.ai.dto.result.AiSubGoalResult;
import org.sopt36.ninedotserver.mandalart.v1.dto.response.AiSubGoalResponse;

import java.util.stream.Collectors;

public class AiSubGoalResponseMapper {
    public static AiSubGoalResponse toAiSubGoalResponse(AiSubGoalResult result) {
        return new AiSubGoalResponse(
                result.aiRecommendedList().stream()
                        .map(aiRecommendedList ->
                                new AiSubGoalResponse.AiSubGoalRecommendationResponse(
                                        aiRecommendedList.title(),
                                        aiRecommendedList.cycle()
                                ))
                        .collect(Collectors.toList())
        );
    }
}
