package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record StreakListResponse(
    List<StreakResponse> streaks
) {

    public static StreakListResponse of(List<StreakResponse> streaks) {
        return new StreakListResponse(List.copyOf(streaks));
    }
}
