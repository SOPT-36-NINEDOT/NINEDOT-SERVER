package org.sopt36.ninedotserver.onboarding.port.out;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.model.Choice;

public interface ChoiceRepositoryPort {

    List<Choice> findAllByActivatedTrue();

    List<Choice> findJobList();
}
