package org.sopt36.ninedotserver.auth.service.logout;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.command.LogoutCommand;
import org.sopt36.ninedotserver.auth.port.in.LogoutUsecase;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LogoutHandler implements LogoutUsecase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Transactional
    @Override
    public void execute(LogoutCommand logoutCommand) {
        refreshTokenRepositoryPort.deleteByUser_Id(logoutCommand.userId());
    }
}
