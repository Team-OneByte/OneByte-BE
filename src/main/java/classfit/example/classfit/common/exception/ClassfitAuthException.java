package classfit.example.classfit.common.exception;

import classfit.example.classfit.common.response.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class ClassfitAuthException extends AuthenticationException {
    private final ErrorCode errorCode;

    public ClassfitAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getHttpStatusCode() {
        return this.errorCode.getStatusCode();
    }
}
