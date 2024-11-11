package classfit.example.classfit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClassfitException extends RuntimeException{
    private final String message;
    private final HttpStatus httpStatus;

    public static final String INVALID_ENTITY_TYPE = "유효하지 않은 엔티티 타입입니다.";

    public ClassfitException(String message,HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
    public int getHttpStatusCode() {
        return this.httpStatus.value();
    }
}
