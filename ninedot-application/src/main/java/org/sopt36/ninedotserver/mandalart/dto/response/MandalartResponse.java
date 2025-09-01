package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.model.Mandalart;

public record MandalartResponse(String title) {

    public static MandalartResponse of(Mandalart mandalart) {
        return new MandalartResponse(mandalart.getTitle());
    }
}
