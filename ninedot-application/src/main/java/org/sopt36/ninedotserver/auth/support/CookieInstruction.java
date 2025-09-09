package org.sopt36.ninedotserver.auth.support;

import java.time.Duration;

public record CookieInstruction(
    String name,
    String value,
    boolean httpOnly,
    boolean secure,
    String path,
    Duration maxAge,
    String sameSite
) {

    public static CookieInstruction httpOnlySecure(
        String name,
        String value,
        Duration maxAge,
        String path,
        String sameSite
    ) {
        return new CookieInstruction(name, value, true, true, path, maxAge, sameSite);
    }
}
