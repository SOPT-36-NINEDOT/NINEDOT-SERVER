package org.sopt36.ninedotserver.mandalart.dto.response;

import java.time.LocalDate;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;

public record MandalartHistoryResponse(
    String title,
    int progressDays
) {

    public static MandalartHistoryResponse of(Mandalart mandalart, LocalDate today) {
        return new MandalartHistoryResponse(mandalart.getTitle(), mandalart.getProgressDays(today));
    }
}
