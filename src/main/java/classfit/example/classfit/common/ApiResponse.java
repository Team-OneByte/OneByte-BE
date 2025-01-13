package classfit.example.classfit.common;

import classfit.example.classfit.common.domain.ResultType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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

    public static void errorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"message\": \"%s\", \"status\": %d}", message, status);
        response.getWriter().write(jsonResponse);
    }

    private record ErrorResult(String message) {

    }
}
