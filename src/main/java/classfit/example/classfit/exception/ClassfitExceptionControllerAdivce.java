package classfit.example.classfit.exception;

import classfit.example.classfit.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ClassfitExceptionControllerAdivce {
    @ExceptionHandler(ClassfitException.class)
    public ResponseEntity<ApiResponse<?>> handlePlanearException(ClassfitException e) {
        log.warn("ClassfitException", e);
        return ResponseEntity.status(e.getHttpStatusCode())
                .body(ApiResponse.fail(e.getHttpStatusCode(),e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException e){
        log.error("Valid Exception");
        return ResponseEntity.status(e.getStatusCode())
                .body(ApiResponse.fail(400,e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception", e);
        return ResponseEntity.status(500)
                .body(ApiResponse.fail(500,e.getMessage()));
    }
}
