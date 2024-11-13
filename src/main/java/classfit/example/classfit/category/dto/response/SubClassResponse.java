package classfit.example.classfit.category.dto.response;

public record SubClassResponse(Long mainClassId, Long subClassId, String subClassName) {

    public static SubClassResponse of(Long mainClassId, Long subClassId, String subClassName) {
        return new SubClassResponse(mainClassId, subClassId, subClassName);
    }
}
