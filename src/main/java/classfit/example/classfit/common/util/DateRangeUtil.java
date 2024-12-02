package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static classfit.example.classfit.common.exception.ClassfitException.INVALID_WEEK_VALUE;

public class DateRangeUtil {
    public static void validateWeekOffset(int weekOffset) {
        if (weekOffset < -4 || weekOffset > 2) {
            throw new ClassfitException(INVALID_WEEK_VALUE, HttpStatus.BAD_REQUEST);
        }
    }

    public static List<LocalDate> getWeekRange(LocalDate date, int weekOffset) {
        validateWeekOffset(weekOffset);

        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY).plusWeeks(weekOffset);
        LocalDate endOfWeek = startOfWeek.with(DayOfWeek.SUNDAY);

        List<LocalDate> weekDates = new ArrayList<>();
        LocalDate currentDate = startOfWeek;
        while (!currentDate.isAfter(endOfWeek)) {
            weekDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return weekDates;
    }
}