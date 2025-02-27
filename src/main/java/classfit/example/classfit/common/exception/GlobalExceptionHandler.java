package classfit.example.classfit.common.exception;

import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.common.response.ErrorCode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.lettuce.core.RedisConnectionException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomApiResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        final String fieldName = e.getName();
        final String value = e.getValue() == null ? "null" : e.getValue().toString();
        final String expectedType = Objects.requireNonNull(e.getRequiredType()).getSimpleName();
        final List<String> errorMessages = List.of(
                String.format("%s 필드의 값이 잘못되었습니다. 값: %s, 기대되는 타입: %s", fieldName, value, expectedType)
        );

        log.error(">>> ArgumentTypeMismatchException: {}", errorMessages);
        return ResponseEntity.badRequest()
                .body(CustomApiResponse.fail(ErrorCode.PARAMETER_INVALID, errorMessages));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException e) {
        final BindingResult bindingResult = e.getBindingResult();
        final List<String> errorMessages = Stream.concat(
                bindingResult.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage),
                bindingResult.getGlobalErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
        ).toList();
        
        log.error(">>> ArgumentNotValidException: {}", errorMessages);
        return ResponseEntity.badRequest()
                .body(CustomApiResponse.fail(ErrorCode.PARAMETER_INVALID, errorMessages));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomApiResponse<?>> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error(">>> MethodNotSupportedException ", e);

        final List<String> errorMessages = List.of("지원되지 않는 HTTP 메서드입니다.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(CustomApiResponse.fail(ErrorCode.METHOD_INVALID, errorMessages));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error(">>> HttpMessageNotReadableException ", e);

        if (e.getCause() instanceof MismatchedInputException mismatchedInputException) {
            String fieldName = mismatchedInputException.getPath().isEmpty() ?
                    "알 수 없는 필드" : mismatchedInputException.getPath().get(0).getFieldName();

            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.fail(ErrorCode.PARAMETER_INVALID, List.of(fieldName + " 필드의 값이 잘못되었습니다.")));
        }
        return ResponseEntity.badRequest()
                .body(CustomApiResponse.fail(ErrorCode.PARAMETER_INVALID, List.of("확인할 수 없는 형태의 데이터가 들어왔습니다")));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleEntityNotFound(EntityNotFoundException e) {
        log.error(">>> EntityNotFoundException ", e);

        final List<String> errorMessages = List.of("객체를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.fail(ErrorCode.ENTITY_NOT_FOUND, errorMessages));
    }

    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<CustomApiResponse<?>> handleRedisConnectionException(final RedisConnectionException e) {
        log.error(">>> RedisConnectionException ", e);
        String errorMessage = "Redis connection error: " + e.getMessage();
        List<String> errorMessages = List.of(errorMessage);

        final CustomApiResponse<?> response = CustomApiResponse.fail(ErrorCode.REDIS_CONNECTION_ERROR, errorMessages);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiResponse<?>> handleException(Exception e) {
        log.error(">>> Exception", e);
        return ResponseEntity.internalServerError()
                .body(CustomApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
