package org.sopt36.ninedotserver.mandalart.dto.response;

import org.sopt36.ninedotserver.mandalart.domain.Mandalart;

public record MandalartCreateResponse(
    Long id,
    String title
) {

    /**
     * Creates a {@code MandalartCreateResponse} from the given {@code Mandalart} domain object.
     *
     * @param mandalart the source {@code Mandalart} object
     * @return a new {@code MandalartCreateResponse} containing the id and title from the provided {@code Mandalart}
     */
    public static MandalartCreateResponse from(Mandalart mandalart) {
        return new MandalartCreateResponse(mandalart.getId(), mandalart.getTitle());
    }
}
