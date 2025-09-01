package org.sopt36.ninedotserver.onboarding.persistence.querydsl;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.model.Choice;

public interface ChoiceRepositoryCustom {

    List<Choice> findJobList();
}
