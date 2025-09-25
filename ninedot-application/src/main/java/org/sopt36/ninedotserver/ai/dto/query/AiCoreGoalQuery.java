package org.sopt36.ninedotserver.ai.dto.query;

import java.util.List;

public record AiCoreGoalQuery(String mandalart, List<CoreGoalUserInputQuery> coreGoal) {
    public record CoreGoalUserInputQuery(
            String title
    ) {

    }
}
