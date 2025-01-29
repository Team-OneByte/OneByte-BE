package classfit.example.classfit.calendarCategory.dto.response;

import classfit.example.classfit.memberCalendar.domain.CalendarType;
import lombok.Builder;

@Builder
public record CalendarCategoryCreateResponse(
    Long id,
    String name,
    String color,
    CalendarType type
){
    public static CalendarCategoryCreateResponse of(final Long id, final String name, final String color, final CalendarType type) {
        return CalendarCategoryCreateResponse.builder()
            .id(id)
            .name(name)
            .color(color)
            .type(type)
            .build();
    }
}
