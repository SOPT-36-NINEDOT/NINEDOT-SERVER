package org.sopt36.ninedotserver.user.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.service.command.UserCommandService;
import org.sopt36.ninedotserver.user.service.query.UserQueryService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

}
