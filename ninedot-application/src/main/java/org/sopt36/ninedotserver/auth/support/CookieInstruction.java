package org.sopt36.ninedotserver.auth.support;

public sealed interface CookieInstruction
    permits CookieInstruction.SetRefreshToken, CookieInstruction.ClearRefreshToken {

    static CookieInstruction setRefreshToken(String refreshToken) {
        return new SetRefreshToken(refreshToken);
    }

    static CookieInstruction clearRefreshToken() {
        return new ClearRefreshToken();
    }


    record SetRefreshToken(String refreshToken) implements CookieInstruction {

    }

    record ClearRefreshToken() implements CookieInstruction {

    }
}

