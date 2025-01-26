package classfit.example.classfit.category.dto.response;

import classfit.example.classfit.category.domain.SubClass;
import lombok.Builder;

@Builder
public record SubClassResponse(
        Long mainClassId,
        Long subClassId,
        String subClassName
) {

    public static SubClassResponse from(SubClass subClass) {
        return SubClassResponse.builder()
                .mainClassId(subClass.getMainClass().getId())
                .subClassId(subClass.getId())
                .subClassName(subClass.getSubClassName())
                .build();
    }
}
