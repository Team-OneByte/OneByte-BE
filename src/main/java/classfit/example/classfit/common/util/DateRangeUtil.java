package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateRangeUtil {
    public static void validateWeekOffset(int weekOffset) {
        if (weekOffset < -4 || weekOffset > 2) {
            throw new ClassfitException(ErrorCode.ATTENDANCE_WEEK_VALUE_INVALID);
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