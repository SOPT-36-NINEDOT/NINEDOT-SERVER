package org.sopt36.ninedotserver.auth.dto.command;

public record GoogleLoginCommand(
    String code,
    String clientRedirectUri
) {

}