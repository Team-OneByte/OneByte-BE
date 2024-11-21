package classfit.example.classfit.common;

import classfit.example.classfit.common.domain.ResultType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int statusCode, ResultType resultType, T data, ErrorResult error,
                             String message) {

    public static <T> ApiResponse<T> success(T data, int statusCode, String message) {
        return new ApiResponse<>(statusCode, ResultType.SUCCESS, data, null, message);
    }

    public static ApiResponse<?> fail(int statusCode, String errorMessage) {
        return new ApiResponse<>(statusCode, ResultType.FAIL, null, new ErrorResult(errorMessage),
            null);
    }

    private record ErrorResult(String message) {

    }
}
