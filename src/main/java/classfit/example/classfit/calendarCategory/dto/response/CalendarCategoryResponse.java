package classfit.example.classfit.calendarCategory.dto.response;

import classfit.example.classfit.calendarCategory.domain.CategoryColor;
import lombok.Builder;

@Builder
public record CalendarCategoryResponse(
    Long id,
    String name,
    String color
){
    public static CalendarCategoryResponse of(
        final Long id,
        final String name,
        final CategoryColor color
    ) {
        return CalendarCategoryResponse.builder()
            .id(id)
            .name(name)
            .color(color.getHexCode())
            .build();
    }
}
