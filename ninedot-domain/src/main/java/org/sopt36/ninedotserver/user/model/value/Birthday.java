package org.sopt36.ninedotserver.user.model.value;

import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record Birthday(String value) {

    public static final int MAX_LENGTH = 20;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");


    public Birthday {
        validateNotBlank(value);
        validateLength(value);
        validateDateFormat(value);
    }

    private static void validateDateFormat(String value) {
        try {
            LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new UserException(UserErrorCode.INVALID_BIRTHDAY_TYPE);
        }
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new UserException(UserErrorCode.INVALID_BIRTHDAY_LENGTH);
        }
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new UserException(UserErrorCode.BIRTHDAY_NOT_BLANK);
        }
    }

    public int calculateAge() {
        return calculateAge(LocalDate.now());
    }

    public int calculateAge(Clock clock) {
        return calculateAge(LocalDate.now(clock));
    }

    private int calculateAge(LocalDate today) {
        LocalDate birthDate = LocalDate.parse(this.value, FORMATTER);
        return Period.between(birthDate, today).getYears();
    }

    public LocalDate toDate() {
        return LocalDate.parse(value, FORMATTER);
    }

    @Override
    public String toString() {
        return value;
    }

}
