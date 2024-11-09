package classfit.example.classfit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClassfitException extends RuntimeException{
    private final String message;
    private final HttpStatus httpStatus;

    public ClassfitException(String message,HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
