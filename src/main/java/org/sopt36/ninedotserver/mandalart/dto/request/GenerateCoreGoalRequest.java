package org.sopt36.ninedotserver.mandalart.dto.request;

import java.util.List;
import org.sopt36.ninedotserver.ai.dto.response.CoreGoalAiTitle;

public record GenerateCoreGoalRequest(String mandalart, List<CoreGoalAiTitle> coreGoal) {

}
