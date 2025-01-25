package classfit.example.classfit.common.exception;

import classfit.example.classfit.common.CustomApiResponse;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ClassfitExceptionControllerAdivce {

    @ExceptionHandler(ClassfitException.class)
    public ResponseEntity<CustomApiResponse<?>> handlePlanearException(ClassfitException e) {
        log.warn("ClassfitException", e);
        return ResponseEntity.status(e.getHttpStatusCode())
            .body(CustomApiResponse.fail(e.getHttpStatusCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.badRequest()
            .body(CustomApiResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("Exception", e);

        if (e.getCause() instanceof MismatchedInputException mismatchedInputException) {
            return ResponseEntity.badRequest()
                .body(CustomApiResponse.fail(HttpStatus.BAD_REQUEST.value(),
                    mismatchedInputException.getPath().get(0).getFieldName() + " 필드의 값이 잘못되었습니다."));
        }

        return ResponseEntity.badRequest()
            .body(CustomApiResponse.fail(HttpStatus.BAD_REQUEST.value(),
                "확인할 수 없는 형태의 데이터가 들어왔습니다"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiResponse<?>> handleException(Exception e) {
        log.error("Exception", e);
        return ResponseEntity.internalServerError()
            .body(CustomApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}
