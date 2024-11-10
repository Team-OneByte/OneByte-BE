package classfit.example.classfit.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int statusCode, ResultType resultType, T data, ErrorResult error) {

    // success 메서드: message는 추가할 수 있지만, null로 하지 않음
    public static <T> ApiResponse<T> success(T data, int statusCode, String message) {
        return new ApiResponse<>(statusCode, ResultType.SUCCESS, data, null);
    }

    // fail 메서드: errorMessage를 ErrorResult로만 전달
    public static ApiResponse<?> fail(int statusCode, String errorMessage) {
        return new ApiResponse<>(statusCode, ResultType.FAIL, null, new ErrorResult(errorMessage));
    }

    private record ErrorResult(String message) {
    }
}
