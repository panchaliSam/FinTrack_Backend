package com.fintrack.utility;

import com.fintrack.type.RecurrenceFrequency;
import java.time.LocalDateTime;

public class RecurrenceUtil {

    public static LocalDateTime calculateNextOccurrence(LocalDateTime currentDate, RecurrenceFrequency frequency) {
        if (currentDate == null || frequency == null) {
            return null;
        }

        switch (frequency) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            case YEARLY:
                return currentDate.plusYears(1);
            default:
                throw new IllegalArgumentException("Invalid recurrence frequency");
        }
    }
}