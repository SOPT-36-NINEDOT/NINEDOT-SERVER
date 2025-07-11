package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.Mandalart;

public record MandalartCreateResponse(
    Long id,
    String title
) {

    public static MandalartCreateResponse from(Mandalart mandalart) {
        return new MandalartCreateResponse(mandalart.getId(), mandalart.getTitle());
    }
}
