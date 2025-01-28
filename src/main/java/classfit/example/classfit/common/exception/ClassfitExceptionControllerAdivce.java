package classfit.example.classfit.common.exception;

import classfit.example.classfit.common.response.CustomApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ClassfitExceptionControllerAdivce {

    @ExceptionHandler(ClassfitException.class)
    public ResponseEntity<CustomApiResponse<?>> handlePlanearException(ClassfitException e) {
        log.warn("ClassfitException", e);
        return ResponseEntity.status(e.getHttpStatusCode())
            .body(CustomApiResponse.fail(e.getErrorCode()));
    }
}
