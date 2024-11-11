package classfit.example.classfit.common.util;

import classfit.example.classfit.attendance.service.util.DateRangeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeUtilTest {

    @Test
    @DisplayName("월요일부터 일요일까지 날짜_일반적인 상황")
    public void testGetWeekRange_WithStandardDate() {
        LocalDate inputDate = LocalDate.of(2024, 11, 10);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 11, 4), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 11, 10), weekRange.get(6));
    }

    @Test
    @DisplayName("월요일부터 일요일까지 날짜_월이 넘어가는 경우_1")
    public void testGetWeekRange_WhenMonthChangesFromOctoberToNovember() {
        LocalDate inputDate = LocalDate.of(2024, 11, 1);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 10, 28), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 11, 3), weekRange.get(6));
    }

    @Test
    @DisplayName("월요일부터 일요일까지 날짜_월이 넘어가는 경우_2")
    public void testGetWeekRange_WhenMonthChangesFromNovemberToDecember() {
        LocalDate inputDate = LocalDate.of(2024, 11, 27);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 11, 25), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 12, 1), weekRange.get(6));
    }
}