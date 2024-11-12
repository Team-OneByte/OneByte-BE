package classfit.example.classfit.category.dto.request;

import classfit.example.classfit.exception.ClassfitException;
import org.springframework.http.HttpStatus;

public record MainClassRequest(String mainClassName) {

    public MainClassRequest {
        if (mainClassName == null || mainClassName.isBlank()) {
            throw new ClassfitException("메인 클래스 이름은 비어 있을 수 없습니다.",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (mainClassName.length() > 20) {
            throw new ClassfitException("메인 클래스 이름은 20자를 초과할 수 없습니다.",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
