package classfit.example.classfit.category.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ClassInfoResponse(
        Long mainClassId,
        String mainClassName,
        List<SubClassResponse> subClasses
) {
    public static ClassInfoResponse of(Long mainClassId, String mainClassName, List<SubClassResponse> subClasses) {
        return ClassInfoResponse.builder()
                .mainClassId(mainClassId)
                .mainClassName(mainClassName)
                .subClasses(subClasses)
                .build();
    }
}
