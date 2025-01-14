package classfit.example.classfit.event.dto.response;

public record EventMonthlyResponse
    (
        String id,
        String name,
        String color,
        String eventType,
        String startDate,
        String endDate
    ) {
    public static EventMonthlyResponse of(final String id, final String name, final String color, final String eventType, final String startDate, final String endDate) {
        return new EventMonthlyResponse(id, name, color, eventType, startDate, endDate);
    }
}
