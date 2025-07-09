package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record CoreGoalIdsResponse(List<CoreGoalIdResponse> coreGoalIds) {

    public static CoreGoalIdsResponse of(List<CoreGoalIdResponse> ids) {
        return new CoreGoalIdsResponse(List.copyOf(ids));
    }
}
