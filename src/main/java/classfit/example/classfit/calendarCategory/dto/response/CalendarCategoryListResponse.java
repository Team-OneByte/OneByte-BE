package classfit.example.classfit.calendarCategory.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CalendarCategoryListResponse(
        List<CalendarCategoryResponse> personalCategories,
        List<CalendarCategoryResponse> sharedCategories
) {
    public static CalendarCategoryListResponse of(final List<CalendarCategoryResponse> personalCategories, final List<CalendarCategoryResponse> sharedCategories) {
        return CalendarCategoryListResponse.builder()
                .personalCategories(personalCategories)
                .sharedCategories(sharedCategories)
                .build();
    }
}