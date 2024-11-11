package classfit.example.classfit.attendance.service.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateRangeUtil {
    public static List<LocalDate> getWeekRange(LocalDate date) {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        List<LocalDate> weekDates = new ArrayList<>();
        LocalDate currentDate = startOfWeek;
        while (!currentDate.isAfter(endOfWeek)) {
            weekDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return weekDates;
    }
}