package org.sopt36.ninedotserver.onboarding.repository;

import org.sopt36.ninedotserver.onboarding.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long>, OptionRepositoryCustom {

}
