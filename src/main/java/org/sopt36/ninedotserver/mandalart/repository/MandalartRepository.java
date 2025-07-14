package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MandalartRepository
    extends JpaRepository<Mandalart, Long>, MandalartRepositoryCustom {

    @Query("SELECT m.id FROM Mandalart m WHERE m.user.id = :userId")
    List<Long> findIdByUserId(@Param("userId") Long userId);
}
