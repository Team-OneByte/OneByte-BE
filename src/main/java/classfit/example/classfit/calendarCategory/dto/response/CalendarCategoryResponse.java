package classfit.example.classfit.calendarCategory.dto.response;

import classfit.example.classfit.memberCalendar.domain.CalendarType;

public record CalendarCategoryResponse
    (
        Long id,
        String name,
        String color,
        CalendarType type
    ){

    public static CalendarCategoryResponse of(final Long id, final String name, final String color, final CalendarType type) {
        return new CalendarCategoryResponse(id, name, color, type);
    }
}
