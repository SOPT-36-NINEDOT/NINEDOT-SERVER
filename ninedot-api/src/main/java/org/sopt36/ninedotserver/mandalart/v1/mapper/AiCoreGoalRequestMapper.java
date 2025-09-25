package org.sopt36.ninedotserver.mandalart.v1.mapper;

import org.sopt36.ninedotserver.ai.dto.query.AiCoreGoalQuery;
import org.sopt36.ninedotserver.mandalart.v1.dto.request.AiCoreGoalRequest;

import java.util.stream.Collectors;

public class AiCoreGoalRequestMapper {

    public static AiCoreGoalQuery toAiCoreGoalQuery(AiCoreGoalRequest request) {
        return new AiCoreGoalQuery(
                request.mandalart(),
                request.coreGoal().stream()
                        .map(coreGoal ->
                                new AiCoreGoalQuery.CoreGoalUserInputQuery(coreGoal.title())
                        )
                        .collect(Collectors.toList()));
    }
}
