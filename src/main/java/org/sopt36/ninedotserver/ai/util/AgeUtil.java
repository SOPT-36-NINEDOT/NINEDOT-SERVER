package org.sopt36.ninedotserver.ai.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static int calculateAgeFromString(String birthDateString) {
        LocalDate birthDate = LocalDate.parse(birthDateString, FORMATTER);
        return calculateAge(birthDate);
    }

    public static int calculateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }
}
