package org.sopt36.ninedotserver.mandalart.repository;

import java.util.List;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

public interface SubGoalSnapshotRepositoryCustom {

    List<SubGoalSnapshot> findByCoreGoalSnapshotIdOrderByPosition(Long coreGoalId);

}
