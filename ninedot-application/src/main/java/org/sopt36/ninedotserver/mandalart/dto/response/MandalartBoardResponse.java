package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;

public record MandalartBoardResponse(
    String title,
    List<CoreGoalBoardResponse> coreGoals
) {

    public static MandalartBoardResponse of(
        Mandalart mandalart,
        List<CoreGoalBoardResponse> coreGoals
    ) {
        return new MandalartBoardResponse(mandalart.getTitle(), List.copyOf(coreGoals));
    }
}
