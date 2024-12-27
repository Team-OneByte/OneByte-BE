package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.EventType;
import java.time.LocalDateTime;
import java.util.Optional;

public record EventModalCreateRequest(
    String name,
    EventType eventType,
    long categoryId,
    LocalDateTime startDate,
    Optional<LocalDateTime> endDate,
    boolean isAllDay
) {
}
