package classfit.example.classfit.event.dto.response;

import classfit.example.classfit.event.domain.EventType;
import java.time.LocalDateTime;

public record EventResponse
    (
        Long id,
        String name,
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
    public static EventResponse of(final Long id, final String name, final EventType eventType, final LocalDateTime startDate, final LocalDateTime endDate) {
        return new EventResponse(id, name, eventType, startDate, endDate);
    }
}
