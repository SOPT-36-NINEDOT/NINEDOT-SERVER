package org.sopt36.ninedotserver.onboarding.repository;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long>, ChoiceRepositoryCustom {
    List<Choice> findAllByActivatedTrue();
}
