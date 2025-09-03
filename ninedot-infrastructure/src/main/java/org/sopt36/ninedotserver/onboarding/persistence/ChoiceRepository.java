package org.sopt36.ninedotserver.onboarding.persistence;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.model.Choice;
import org.sopt36.ninedotserver.onboarding.persistence.querydsl.ChoiceRepositoryCustom;
import org.sopt36.ninedotserver.onboarding.port.out.ChoiceRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository
    extends JpaRepository<Choice, Long>, ChoiceRepositoryPort, ChoiceRepositoryCustom {

    List<Choice> findAllByActivatedTrue();
}
