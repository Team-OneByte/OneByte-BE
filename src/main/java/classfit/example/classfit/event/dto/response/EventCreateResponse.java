package classfit.example.classfit.event.dto.response;

import java.time.LocalDateTime;

public record EventCreateResponse
    (
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
    public static EventCreateResponse of(final Long id, final String name, final LocalDateTime startDate, final LocalDateTime endDate) {
        return new EventCreateResponse(id, name, startDate, endDate);
    }
}
