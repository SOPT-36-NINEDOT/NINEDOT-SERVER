package org.sopt36.ninedotserver.user.model;

import lombok.Getter;

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

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

}
