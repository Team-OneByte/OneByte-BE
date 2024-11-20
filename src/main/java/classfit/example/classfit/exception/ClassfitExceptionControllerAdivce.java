package classfit.example.classfit.exception;

import classfit.example.classfit.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ClassfitExceptionControllerAdivce {

    @ExceptionHandler(ClassfitException.class)
    public ResponseEntity<ApiResponse<?>> handlePlanearException(ClassfitException e) {
        log.warn("ClassfitException", e);
        return ResponseEntity.status(e.getHttpStatusCode())
            .body(ApiResponse.fail(e.getHttpStatusCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.badRequest()
            .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception", e);
        return ResponseEntity.internalServerError()
            .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}
