package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public interface CoreGoalSnapshotRepositoryCustom {

    List<CoreGoalSnapshot> findByMandalartIdOrderByPosition(Long mandalartId);

    List<String> findActiveCoreGoalTitleByMandalartId(Long mandalartId);
}
