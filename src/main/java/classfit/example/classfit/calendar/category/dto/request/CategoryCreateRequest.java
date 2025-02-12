package classfit.example.classfit.calendar.category.dto.request;

import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;

public record CategoryCreateRequest(
        String name,
        String color,
        CalendarType type
) {
}