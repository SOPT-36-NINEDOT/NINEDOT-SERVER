package org.sopt36.ninedotserver.mandalart.persistence;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.mandalart.persistence.querydsl.MandalartRepositoryCustom;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MandalartRepository
    extends JpaRepository<Mandalart, Long>, MandalartRepositoryPort, MandalartRepositoryCustom {

    @Query("SELECT m.id FROM Mandalart m WHERE m.user.id = :userId")
    List<Long> findIdByUserId(@Param("userId") Long userId);
}
