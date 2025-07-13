package org.sopt36.ninedotserver.onboarding.dto.response;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.domain.Choice;

public record JobDropdownResponse(
    List<Job> jobList
) {

    public static JobDropdownResponse of(List<Job> jobList) {
        return new JobDropdownResponse(jobList);
    }

    public record Job(Long id, String job) {

        public static Job from(Choice choice) {
            return new Job(choice.getId(), choice.getContent());
        }
    }
}
