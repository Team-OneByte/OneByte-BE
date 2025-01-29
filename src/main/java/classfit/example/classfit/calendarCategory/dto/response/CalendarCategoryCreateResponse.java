package classfit.example.classfit.calendarCategory.dto.response;

import classfit.example.classfit.memberCalendar.domain.CalendarType;

public record CalendarCategoryCreateResponse(
    Long id,
    String name,
    String color,
    CalendarType type
){
    public static CalendarCategoryCreateResponse of(
        final Long id,
        final String name,
        final String color,
        final CalendarType type
    ) {
        return new CalendarCategoryCreateResponse(id, name, color, type);
    }
}
