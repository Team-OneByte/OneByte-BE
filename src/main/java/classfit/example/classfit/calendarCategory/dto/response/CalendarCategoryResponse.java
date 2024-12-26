package classfit.example.classfit.calendarCategory.dto.response;

public record CalendarCategoryResponse
    (
        Long id,
        String name,
        String color
    ){

    public static CalendarCategoryResponse of(final Long id, final String name, final String color) {
        return new CalendarCategoryResponse(id, name, color);
    }
}
