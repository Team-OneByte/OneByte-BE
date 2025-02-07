package classfit.example.classfit.calendar.category.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryListResponse(
        List<CategoryResponse> personalCategories,
        List<CategoryResponse> sharedCategories
) {
    public static CategoryListResponse of(final List<CategoryResponse> personalCategories, final List<CategoryResponse> sharedCategories) {
        return CategoryListResponse.builder()
                .personalCategories(personalCategories)
                .sharedCategories(sharedCategories)
                .build();
    }
}