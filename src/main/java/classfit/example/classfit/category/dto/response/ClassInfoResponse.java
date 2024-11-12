package classfit.example.classfit.category.dto.response;

import java.util.List;

public record ClassInfoResponse(
        Long mainClassId,
        String mainClassName,
        List<SubClassResponse> subClasses) {

    public static ClassInfoResponse of(Long mainClassId, String mainClassName, List<SubClassResponse> subClasses) {
        return new ClassInfoResponse(mainClassId, mainClassName, subClasses);
    }
}
