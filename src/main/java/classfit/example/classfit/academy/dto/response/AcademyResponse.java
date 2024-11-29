package classfit.example.classfit.academy.dto.response;

import classfit.example.classfit.academy.domain.Academy;
import lombok.Builder;

@Builder
public record AcademyResponse
    (
        Long id,

        String name
    ) {

    public static AcademyResponse from(Academy academy) {
        return AcademyResponse.builder()
            .id(academy.getId())
            .name(academy.getName())
            .build();
    }
}
