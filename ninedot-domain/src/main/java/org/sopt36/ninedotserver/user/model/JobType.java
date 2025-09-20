package org.sopt36.ninedotserver.user.model;

import lombok.Getter;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum JobType {
    STUDENT("학생"),
    JOB_SEEKER("취업 준비생 / 휴직자"),
    OFFICE_WORKER("사무/행정직"),
    PLANNER("기획/전략/PM"),
    DEVELOPER("개발자 / 데이터직"),
    DESIGNER("디자이너 / 크리에이터"),
    MARKETER("마케터 / 콘텐츠 운영자"),
    SALES("영업 / 고객 응대"),
    EDUCATOR("교육자 / 연구자"),
    MEDICAL("의료/복지/상담직"),
    FREELANCER("자영업 / 프리랜서"),
    TECHNICIAN("기술직 / 현장직"),
    ARTIST("예술/공연/문화 종사자"),
    OTHER("기타 / 직접 입력");

    public static final int MAX_LENGTH = 100;

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

    public static JobType from(String raw) {
        validateNotBlank(raw);
        return findByName(raw)
                .or(() -> findByDisplayName(raw))
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_JOB_VALUE));
    }

    private static void validateNotBlank(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new UserException(UserErrorCode.JOB_NOT_NULL);
        }
    }

    private static Optional<JobType> findByName(String raw) {
        return Arrays.stream(JobType.values())
                .filter(type -> type.name().equalsIgnoreCase(raw))
                .findFirst();
    }

    private static Optional<JobType> findByDisplayName(String raw) {
        return Arrays.stream(JobType.values())
                .filter(type -> type.displayName.equalsIgnoreCase(raw))
                .findFirst();
    }

}
