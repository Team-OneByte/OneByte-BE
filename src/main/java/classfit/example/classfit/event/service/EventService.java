package classfit.example.classfit.event.service;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.service.CalendarCategoryService;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.NotificationTime;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.response.EventCreateResponse;
import classfit.example.classfit.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CalendarCategoryService calendarCategoryService;

    @Transactional
    public EventCreateResponse createEvent(EventCreateRequest request) {
        Event event = buildEvent(request);
        Event savedEvent = eventRepository.save(event);
        return EventCreateResponse.of(savedEvent.getId(), savedEvent.getName(), savedEvent.getStartDate(), savedEvent.getEndDate());
    }

    private Event buildEvent(EventCreateRequest request) {
        CalendarCategory category = calendarCategoryService.getCategoryById(request.categoryId());
        NotificationTime notificationTime = NotificationTime.valueOf(request.notificationTime());

        return Event.builder()
            .name(request.name())
            .category(category)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .isAllDay(request.isAllDay())
            .isRepeating(request.isRepeating())
            .notificationTime(notificationTime)
            .location(request.location())
            .memo(request.memo())
            .build();
    }
}
