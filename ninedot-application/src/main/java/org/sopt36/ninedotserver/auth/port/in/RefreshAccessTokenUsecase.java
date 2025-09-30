package org.sopt36.ninedotserver.auth.port.in;

import org.sopt36.ninedotserver.auth.dto.command.RefreshCommand;
import org.sopt36.ninedotserver.auth.dto.result.RefreshResult;

public interface RefreshAccessTokenUsecase {

    RefreshResult execute(RefreshCommand refreshCommand);
}
