package classfit.example.classfit.calendarCategory.dto.response;

import java.util.List;

public record CalendarCategoryListResponse
    (
        List<CalendarCategoryResponse> personalCategories,
        List<CalendarCategoryResponse> sharedCategories
    ){

    public static CalendarCategoryListResponse of(final List<CalendarCategoryResponse> personalCategories, final List<CalendarCategoryResponse> sharedCategories) {
        return new CalendarCategoryListResponse(personalCategories, sharedCategories);
    }
}