package classfit.example.classfit.attendance.dto.response;

public record StatisticsMemberResponse(
    String name,
    int present,
    int absent,
    int late,
    int extra) {

    public static StatisticsMemberResponse of(final String name, final int present, final int absent,
        final int late, final int extra) {
        return new StatisticsMemberResponse(name, present, absent, late, 0);
    }
}
