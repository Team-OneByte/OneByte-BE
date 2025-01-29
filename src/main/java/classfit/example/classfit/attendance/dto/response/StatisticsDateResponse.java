package classfit.example.classfit.attendance.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StatisticsDateResponse(
    LocalDate date,
    int week,
    int present,
    int absent,
    int late,
    int extra
) {
    public static StatisticsDateResponse of(
        final LocalDate date,
        final int week,
        final int present,
        final int absent,
        final int late,
        final int extra
    ) {
        return StatisticsDateResponse.builder()
            .date(date)
            .week(week)
            .present(present)
            .absent(absent)
            .late(late)
            .extra(0)
            .build();
    }
}
