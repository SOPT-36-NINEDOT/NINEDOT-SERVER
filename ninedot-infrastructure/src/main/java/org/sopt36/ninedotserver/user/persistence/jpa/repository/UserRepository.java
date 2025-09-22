package org.sopt36.ninedotserver.user.persistence.jpa.repository;

import org.sopt36.ninedotserver.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
