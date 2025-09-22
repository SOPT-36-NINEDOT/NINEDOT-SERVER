package org.sopt36.ninedotserver.user.port.out;

import org.sopt36.ninedotserver.user.model.User;

public interface UserCommandPort {

    <S extends User> S save(S user);
}
