package classfit.example.classfit.calendar.event.dto.response;

import classfit.example.classfit.calendar.event.domain.enumType.EventType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventResponse(
        Long id,
        String name,
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public static EventResponse of(final Long id, final String name, final EventType eventType, final LocalDateTime startDate, final LocalDateTime endDate) {
        return EventResponse.builder()
                .id(id)
                .name(name)
                .eventType(eventType)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
