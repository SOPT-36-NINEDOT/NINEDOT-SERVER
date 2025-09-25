package org.sopt36.ninedotserver.mandalart.v1.mapper;

import org.sopt36.ninedotserver.ai.dto.result.AiCoreGoalResult;
import org.sopt36.ninedotserver.mandalart.v1.dto.response.AiCoreGoalResponse;

import java.util.stream.Collectors;

public class AiCoreGoalResponseMapper {

    public static AiCoreGoalResponse toAiCoreGoalResponse(AiCoreGoalResult result) {
        return new AiCoreGoalResponse(
                result.aiRecommendedList().stream()
                        .map(aiRecommendedList ->
                                new AiCoreGoalResponse.AiCoreGoalRecommendationResponse(
                                        aiRecommendedList.title()
                                ))
                        .collect(Collectors.toList())
        );
    }
}
