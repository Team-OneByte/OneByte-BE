package classfit.example.classfit.calendarCategory.dto.request;

import classfit.example.classfit.memberCalendar.domain.CalendarType;

public record CalendarCategoryCreateRequest(
    String name,
    String color,
    CalendarType type) {
}