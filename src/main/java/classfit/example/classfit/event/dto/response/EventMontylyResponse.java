package classfit.example.classfit.event.dto.response;

public record EventMontylyResponse
    (
        String id,
        String name,
        String eventType,
        String startDate,
        String endDate
    ) {
    public static EventMontylyResponse of(final String id, final String name, final String eventType, final String startDate, final String endDate) {
        return new EventMontylyResponse(id, name, eventType, startDate, endDate);
    }
}
