package classfit.example.classfit.calendar.event.dto.response;

import lombok.Builder;

@Builder
public record EventMonthlyResponse(
        String id,
        String name,
        String color,
        String eventType,
        String startDate,
        String endDate
) {
    public static EventMonthlyResponse of(final String id, final String name, final String color, final String eventType, final String startDate, final String endDate) {
        return EventMonthlyResponse.builder()
                .id(id)
                .name(name)
                .color(color)
                .eventType(eventType)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
