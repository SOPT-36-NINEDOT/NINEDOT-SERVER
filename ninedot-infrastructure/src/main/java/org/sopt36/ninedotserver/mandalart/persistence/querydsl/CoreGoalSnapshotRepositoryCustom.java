package org.sopt36.ninedotserver.mandalart.persistence.querydsl;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;

public interface CoreGoalSnapshotRepositoryCustom {

    List<CoreGoalSnapshot> findByMandalartIdOrderByPosition(Long mandalartId);

    List<String> findActiveCoreGoalTitleByMandalartId(Long mandalartId);
}
