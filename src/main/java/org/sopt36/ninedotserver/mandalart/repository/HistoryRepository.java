package org.sopt36.ninedotserver.mandalart.repository;

import org.sopt36.ninedotserver.mandalart.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

}
