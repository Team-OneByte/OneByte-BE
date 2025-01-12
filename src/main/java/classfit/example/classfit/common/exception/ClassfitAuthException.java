package classfit.example.classfit.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class ClassfitAuthException extends AuthenticationException {

    private final HttpStatus httpStatus;

    public ClassfitAuthException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
