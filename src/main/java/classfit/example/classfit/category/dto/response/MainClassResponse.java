package classfit.example.classfit.category.dto.response;

import classfit.example.classfit.category.domain.MainClass;
import lombok.Builder;

@Builder
public record MainClassResponse(
        Long mainClassId,
        String mainClassName
) {
    public static MainClassResponse from(MainClass mainClass) {
        return MainClassResponse.builder()
                .mainClassId(mainClass.getId())
                .mainClassName(mainClass.getMainClassName())
                .build();
    }
}
