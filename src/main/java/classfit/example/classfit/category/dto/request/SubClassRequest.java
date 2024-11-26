package classfit.example.classfit.category.dto.request;

import classfit.example.classfit.common.exception.ClassfitException;
import org.springframework.http.HttpStatus;

public record SubClassRequest(Long mainClassId, String subClassName) {

    public SubClassRequest {
        if (subClassName == null || subClassName.isBlank()) {
            throw new ClassfitException("서브 클래스 이름은 비어 있을 수 없습니다.",
                HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (subClassName.length() > 20) {
            throw new ClassfitException("서브 클래스 이름은 20자를 초과할 수 없습니다.",
                HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
