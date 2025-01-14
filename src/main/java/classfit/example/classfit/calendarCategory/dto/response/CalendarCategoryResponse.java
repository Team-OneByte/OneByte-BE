package classfit.example.classfit.calendarCategory.dto.response;

import classfit.example.classfit.calendarCategory.domain.CategoryColor;

public record CalendarCategoryResponse
    (
        Long id,
        String name,
        String color
    ){

    public static CalendarCategoryResponse of(final Long id, final String name, final CategoryColor color) {
        return new CalendarCategoryResponse(id, name, color.getHexCode());
    }
}
