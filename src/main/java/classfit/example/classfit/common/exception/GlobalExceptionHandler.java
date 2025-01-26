package classfit.example.classfit.common.exception;

import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.common.response.ErrorCode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());

        log.error("Validation error: {}", errorMessages);
        return ResponseEntity.badRequest()
            .body(CustomApiResponse.fail(ErrorCode.INVALID_PARAMETER, errorMessages));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("Exception", e);

        if (e.getCause() instanceof MismatchedInputException mismatchedInputException) {
            String fieldName = mismatchedInputException.getPath().isEmpty() ?
                "알 수 없는 필드" : mismatchedInputException.getPath().get(0).getFieldName();

            return ResponseEntity.badRequest()
                .body(CustomApiResponse.fail(ErrorCode.INVALID_PARAMETER, List.of(fieldName + " 필드의 값이 잘못되었습니다.")));
        }

        return ResponseEntity.badRequest()
            .body(CustomApiResponse.fail(ErrorCode.INVALID_PARAMETER, List.of("확인할 수 없는 형태의 데이터가 들어왔습니다")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiResponse<?>> handleException(Exception e) {
        log.error("Exception", e);
        return ResponseEntity.internalServerError()
            .body(CustomApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
