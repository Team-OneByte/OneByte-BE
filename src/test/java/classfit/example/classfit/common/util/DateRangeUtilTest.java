package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.util.DateRangeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static classfit.example.classfit.common.exception.ClassfitException.INVALID_WEEK_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateRangeUtilTest {

    @Test
    @DisplayName("이번 주 조회 테스트")
    public void testGetWeekRange_WithStandardDate() {
        LocalDate inputDate = LocalDate.of(2024, 11, 10);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate, 0);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 11, 4), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 11, 10), weekRange.get(6));
    }

    @Test
    @DisplayName("이번 주 조회 테스트_월이 넘어가는 경우_1")
    public void testGetWeekRange_WhenMonthChangesFromOctoberToNovember() {
        LocalDate inputDate = LocalDate.of(2024, 11, 1);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate, 0);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 10, 28), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 11, 3), weekRange.get(6));
    }

    @Test
    @DisplayName("이번 주 조회 테스트_월이 넘어가는 경우_2")
    public void testGetWeekRange_WhenMonthChangesFromNovemberToDecember() {
        LocalDate inputDate = LocalDate.of(2024, 11, 27);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate, 0);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 11, 25), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 12, 1), weekRange.get(6));
    }

    @Test
    @DisplayName("4주 전 조회 테스트")
    public void testGetWeekRange_WhenOffsetIsMinusFour() {
        LocalDate inputDate = LocalDate.of(2024, 11, 27);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate, -4);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 10, 28), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 11, 3), weekRange.get(6));
    }

    @Test
    @DisplayName("2주 후 조회 테스트")
    public void testGetWeekRange_WhenOffsetIsPlusTwo() {
        LocalDate inputDate = LocalDate.of(2024, 11, 27);
        List<LocalDate> weekRange = DateRangeUtil.getWeekRange(inputDate, 2);

        assertEquals(7, weekRange.size());
        assertEquals(LocalDate.of(2024, 12, 9), weekRange.get(0));
        assertEquals(LocalDate.of(2024, 12, 15), weekRange.get(6));
    }

    @Test
    @DisplayName("허용 범위를 벗어난 -5주 조회 시 예외 발생 테스트")
    public void testGetWeekRange_WhenOffsetIsMinusFive_ShouldThrowException() {
        LocalDate inputDate = LocalDate.of(2024, 11, 27);

        ClassfitException exception = assertThrows(ClassfitException.class, () -> {
            DateRangeUtil.getWeekRange(inputDate, -5);
        });

        assertEquals(INVALID_WEEK_VALUE, exception.getMessage());
    }

    @Test
    @DisplayName("허용 범위를 벗어난 +3주 조회 시 예외 발생 테스트")
    public void testGetWeekRange_WhenOffsetIsPlusThree_ShouldThrowException() {
        LocalDate inputDate = LocalDate.of(2024, 11, 27);

        ClassfitException exception = assertThrows(ClassfitException.class, () -> {
            DateRangeUtil.getWeekRange(inputDate, 3);
        });

        assertEquals(INVALID_WEEK_VALUE, exception.getMessage());
    }
}