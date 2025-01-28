package classfit.example.classfit.common.exception;

import classfit.example.classfit.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class ClassfitException extends RuntimeException {
    private final ErrorCode errorCode;

    public ClassfitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getHttpStatusCode() {
        return this.errorCode.getStatusCode();
    }
}
