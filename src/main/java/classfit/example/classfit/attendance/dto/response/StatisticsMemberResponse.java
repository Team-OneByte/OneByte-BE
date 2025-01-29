package classfit.example.classfit.attendance.dto.response;

import lombok.Builder;

@Builder
public record StatisticsMemberResponse(
    Long studentId,
    String name,
    int present,
    int absent,
    int late,
    int extra
) {
    public static StatisticsMemberResponse of(
        final Long studentId,
        final String name,
        final int present,
        final int absent,
        final int late,
        final int extra
    ) {
        return StatisticsMemberResponse.builder()
            .studentId(studentId)
            .name(name)
            .present(present)
            .absent(absent)
            .late(late)
            .extra(extra)
            .build();
    }
}
