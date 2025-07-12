package org.sopt36.ninedotserver.mandalart.repository;

import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MandalartRepository
    extends JpaRepository<Mandalart, Long>, MandalartRepositoryCustom {

}
