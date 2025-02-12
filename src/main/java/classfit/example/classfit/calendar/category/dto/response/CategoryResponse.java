package classfit.example.classfit.calendar.category.dto.response;

import classfit.example.classfit.calendar.category.domain.enumType.CategoryColor;
import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        String color
) {
    public static CategoryResponse of(final Long id, final String name, final CategoryColor color) {
        return CategoryResponse.builder()
                .id(id)
                .name(name)
                .color(color.getHexCode())
                .build();
    }
}
