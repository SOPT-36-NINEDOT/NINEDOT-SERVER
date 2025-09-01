package org.sopt36.ninedotserver.user.support;

import static org.sopt36.ninedotserver.user.exception.UserErrorCode.BIRTHDAY_NOT_BLANK;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.INVALID_BIRTHDAY_TYPE;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.sopt36.ninedotserver.user.exception.UserException;

public class AgeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static int calculateAgeFromString(String birthdayString) {
        if (birthdayString == null || birthdayString.trim().isEmpty()) {
            throw new UserException(BIRTHDAY_NOT_BLANK);
        }
        try {
            LocalDate birthDate = LocalDate.parse(birthdayString, FORMATTER);
            return calculateAge(birthDate);
        } catch (DateTimeParseException e) {
            throw new UserException(INVALID_BIRTHDAY_TYPE);
        }
    }

    private static int calculateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }
}
