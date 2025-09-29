package org.sopt36.ninedotserver.auth.port.in;

import org.sopt36.ninedotserver.auth.dto.command.LogoutCommand;

public interface LogoutUsecase {

    void execute(LogoutCommand logoutCommand);
}
