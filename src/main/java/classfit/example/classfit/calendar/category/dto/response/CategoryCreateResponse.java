package classfit.example.classfit.calendar.category.dto.response;

import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import lombok.Builder;

@Builder
public record CategoryCreateResponse(
        Long id,
        String name,
        String color,
        CalendarType type
) {
    public static CategoryCreateResponse of(final Long id, final String name, final String color, final CalendarType type) {
        return CategoryCreateResponse.builder()
                .id(id)
                .name(name)
                .color(color)
                .type(type)
                .build();
    }
}
