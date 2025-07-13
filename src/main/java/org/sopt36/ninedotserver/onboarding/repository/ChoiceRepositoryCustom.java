package org.sopt36.ninedotserver.onboarding.repository;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.domain.Choice;

public interface ChoiceRepositoryCustom {

    List<Choice> findJobList();
}
