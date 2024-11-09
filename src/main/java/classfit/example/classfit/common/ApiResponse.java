package classfit.example.classfit.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(ResultType resultType, T success, ErrorResult error) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static ApiResponse<?> fail(String errorMessage) {
        return new ApiResponse<>(ResultType.FAIL, null, new ErrorResult(errorMessage));
    }

    private record ErrorResult(String message) {
    }
}
