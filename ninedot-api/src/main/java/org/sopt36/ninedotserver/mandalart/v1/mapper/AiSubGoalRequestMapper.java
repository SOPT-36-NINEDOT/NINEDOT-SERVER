package org.sopt36.ninedotserver.mandalart.v1.mapper;

import org.sopt36.ninedotserver.ai.dto.query.AiSubGoalQuery;
import org.sopt36.ninedotserver.mandalart.v1.dto.request.AiSubGoalRequest;

import java.util.stream.Collectors;

public class AiSubGoalRequestMapper {

    public static AiSubGoalQuery toAiSubGoalQuery(AiSubGoalRequest request) {
        return new AiSubGoalQuery(
                request.coreGoal(),
                request.subGoal().stream()
                        .map(subGoal ->
                                new AiSubGoalQuery.SubGoalUserInputQuery(subGoal.title())
                        )
                        .collect(Collectors.toList()));
    }
}
