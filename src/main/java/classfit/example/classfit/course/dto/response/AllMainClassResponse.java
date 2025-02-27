package classfit.example.classfit.course.dto.response;

import classfit.example.classfit.course.domain.MainClass;
import lombok.Builder;

@Builder
public record AllMainClassResponse(
        Long mainClassId,
        String mainClassName
) {
    public static AllMainClassResponse from(MainClass mainClass) {
        return AllMainClassResponse.builder()
                .mainClassId(mainClass.getId())
                .mainClassName(mainClass.getMainClassName())
                .build();

    }
}
