package org.sopt36.ninedotserver.mandalart.dto.request;

import java.util.List;

public record SubGoalAiListCreateRequest(
    List<SubGoalAiCreateRequest> goals
) {

}
