package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;

public interface CoreGoalSnapshotRepositoryCustom {

    List<CoreGoalSnapshot> findByMandalartIdOrderByPosition(Long mandalartId);
}
