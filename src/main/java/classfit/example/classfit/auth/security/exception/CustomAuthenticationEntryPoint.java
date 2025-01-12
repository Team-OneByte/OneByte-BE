package classfit.example.classfit.auth.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Throwable exception = (Throwable) request.getAttribute("AuthException");

        if (exception instanceof ClassfitAuthException classfitAuthException) {
            writeErrorResponse(response, classfitAuthException.getMessage(), classfitAuthException.getHttpStatusCode());
            return;
        }
        writeErrorResponse(response, "아이디 또는 비밀번호가 잘못 되었습니다.", HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"message\": \"%s\", \"status\": %d}", message, status);
        response.getWriter().write(jsonResponse);
    }
}